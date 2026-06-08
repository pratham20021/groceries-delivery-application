locals {
  prefix = "${var.project}-${var.environment}"

  # MySQL host extracted from endpoint (host:port)
  mysql_host = split(":", var.mysql_endpoint)[0]

  mysql_url_base = "jdbc:mysql://${local.mysql_host}:3306"
  mysql_opts     = "useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true"

  consul_host = "127.0.0.1" # consul runs as sidecar in same task network namespace

  common_env = [
    { name = "SPRING_CLOUD_CONSUL_HOST", value = aws_service_discovery_private_dns_namespace.main.name },
    { name = "SPRING_CLOUD_CONSUL_PORT", value = "8500" },
    { name = "SPRING_PROFILES_ACTIVE",   value = "prod" },
  ]
}

# ── Cluster ───────────────────────────────────────────────────────────────────
resource "aws_ecs_cluster" "main" {
  name = "${local.prefix}-cluster"

  setting {
    name  = "containerInsights"
    value = "enabled"
  }

  tags = { Name = "${local.prefix}-cluster" }
}

resource "aws_ecs_cluster_capacity_providers" "main" {
  cluster_name       = aws_ecs_cluster.main.name
  capacity_providers = ["FARGATE", "FARGATE_SPOT"]

  default_capacity_provider_strategy {
    capacity_provider = "FARGATE"
    weight            = 1
  }
}

# ── Service Discovery (Cloud Map) ─────────────────────────────────────────────
resource "aws_service_discovery_private_dns_namespace" "main" {
  name        = "${local.prefix}.local"
  description = "Private DNS namespace for ${local.prefix}"
  vpc         = var.vpc_id
}

# ── CloudWatch Log Group ──────────────────────────────────────────────────────
resource "aws_cloudwatch_log_group" "main" {
  name              = "/ecs/${local.prefix}"
  retention_in_days = 7
  tags              = { Name = "${local.prefix}-logs" }
}

# ── Consul ────────────────────────────────────────────────────────────────────
resource "aws_service_discovery_service" "consul" {
  name = "consul"
  dns_config {
    namespace_id   = aws_service_discovery_private_dns_namespace.main.id
    routing_policy = "MULTIVALUE"
    dns_records { ttl = 10; type = "A" }
  }
}

resource "aws_ecs_task_definition" "consul" {
  family                   = "${local.prefix}-consul"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "256"
  memory                   = "512"
  execution_role_arn       = var.execution_role_arn
  task_role_arn            = var.task_role_arn

  container_definitions = jsonencode([{
    name      = "consul"
    image     = "hashicorp/consul:latest"
    essential = true
    command   = ["agent", "-server", "-ui", "-bootstrap-expect=1", "-client=0.0.0.0"]
    portMappings = [
      { containerPort = 8500, protocol = "tcp" },
      { containerPort = 8600, protocol = "udp" }
    ]
    environment = [
      { name = "CONSUL_BIND_INTERFACE", value = "eth0" }
    ]
    logConfiguration = {
      logDriver = "awslogs"
      options = {
        awslogs-group         = aws_cloudwatch_log_group.main.name
        awslogs-region        = var.aws_region
        awslogs-stream-prefix = "consul"
      }
    }
  }])
}

resource "aws_ecs_service" "consul" {
  name            = "${local.prefix}-consul"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.consul.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = var.private_subnet_ids
    security_groups  = [var.security_group_id]
    assign_public_ip = false
  }

  service_registries {
    registry_arn = aws_service_discovery_service.consul.arn
  }
}

# ── api-gateway ───────────────────────────────────────────────────────────────
resource "aws_ecs_task_definition" "api_gateway" {
  family                   = "${local.prefix}-api-gateway"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "512"
  memory                   = "1024"
  execution_role_arn       = var.execution_role_arn
  task_role_arn            = var.task_role_arn

  container_definitions = jsonencode([{
    name      = "api-gateway"
    image     = "${var.ecr_repositories["api-gateway"]}:latest"
    essential = true
    portMappings = [{ containerPort = 8080, protocol = "tcp" }]
    environment = concat(local.common_env, [
      { name = "SPRING_CLOUD_CONSUL_HOST", value = "consul.${local.prefix}.local" }
    ])
    logConfiguration = {
      logDriver = "awslogs"
      options = {
        awslogs-group         = aws_cloudwatch_log_group.main.name
        awslogs-region        = var.aws_region
        awslogs-stream-prefix = "api-gateway"
      }
    }
  }])

  depends_on = [aws_ecs_service.consul]
}

resource "aws_ecs_service" "api_gateway" {
  name            = "${local.prefix}-api-gateway"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.api_gateway.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = var.private_subnet_ids
    security_groups  = [var.security_group_id]
    assign_public_ip = false
  }

  load_balancer {
    target_group_arn = var.api_gateway_tg_arn
    container_name   = "api-gateway"
    container_port   = 8080
  }

  depends_on = [aws_ecs_service.consul]
}

# ── auth-service ──────────────────────────────────────────────────────────────
resource "aws_ecs_task_definition" "auth_service" {
  family                   = "${local.prefix}-auth-service"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "512"
  memory                   = "1024"
  execution_role_arn       = var.execution_role_arn
  task_role_arn            = var.task_role_arn

  container_definitions = jsonencode([{
    name      = "auth-service"
    image     = "${var.ecr_repositories["auth-service"]}:latest"
    essential = true
    portMappings = [{ containerPort = 8081, protocol = "tcp" }]
    environment = concat(local.common_env, [
      { name = "SPRING_CLOUD_CONSUL_HOST",    value = "consul.${local.prefix}.local" },
      { name = "SPRING_DATASOURCE_URL",       value = "${local.mysql_url_base}/revcart_auth?${local.mysql_opts}" },
      { name = "SPRING_DATASOURCE_USERNAME",  value = "root" },
      { name = "SPRING_DATASOURCE_PASSWORD",  value = var.db_password },
      { name = "MAIL_USERNAME",               value = var.mail_username },
      { name = "MAIL_PASSWORD",               value = var.mail_password },
      { name = "GOOGLE_CLIENT_ID",            value = var.google_client_id },
      { name = "GOOGLE_CLIENT_SECRET",        value = var.google_client_secret },
    ])
    logConfiguration = {
      logDriver = "awslogs"
      options = {
        awslogs-group         = aws_cloudwatch_log_group.main.name
        awslogs-region        = var.aws_region
        awslogs-stream-prefix = "auth-service"
      }
    }
  }])
}

resource "aws_ecs_service" "auth_service" {
  name            = "${local.prefix}-auth-service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.auth_service.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = var.private_subnet_ids
    security_groups  = [var.security_group_id]
    assign_public_ip = false
  }

  depends_on = [aws_ecs_service.consul]
}

# ── user-service ──────────────────────────────────────────────────────────────
resource "aws_ecs_task_definition" "user_service" {
  family                   = "${local.prefix}-user-service"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "512"
  memory                   = "1024"
  execution_role_arn       = var.execution_role_arn
  task_role_arn            = var.task_role_arn

  container_definitions = jsonencode([{
    name      = "user-service"
    image     = "${var.ecr_repositories["user-service"]}:latest"
    essential = true
    portMappings = [{ containerPort = 8082, protocol = "tcp" }]
    environment = concat(local.common_env, [
      { name = "SPRING_CLOUD_CONSUL_HOST",   value = "consul.${local.prefix}.local" },
      { name = "SPRING_DATASOURCE_URL",      value = "${local.mysql_url_base}/revcart_users?${local.mysql_opts}" },
      { name = "SPRING_DATASOURCE_USERNAME", value = "root" },
      { name = "SPRING_DATASOURCE_PASSWORD", value = var.db_password },
    ])
    logConfiguration = {
      logDriver = "awslogs"
      options = {
        awslogs-group         = aws_cloudwatch_log_group.main.name
        awslogs-region        = var.aws_region
        awslogs-stream-prefix = "user-service"
      }
    }
  }])
}

resource "aws_ecs_service" "user_service" {
  name            = "${local.prefix}-user-service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.user_service.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = var.private_subnet_ids
    security_groups  = [var.security_group_id]
    assign_public_ip = false
  }

  depends_on = [aws_ecs_service.consul]
}

# ── product-service ───────────────────────────────────────────────────────────
resource "aws_ecs_task_definition" "product_service" {
  family                   = "${local.prefix}-product-service"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "512"
  memory                   = "1024"
  execution_role_arn       = var.execution_role_arn
  task_role_arn            = var.task_role_arn

  container_definitions = jsonencode([{
    name      = "product-service"
    image     = "${var.ecr_repositories["product-service"]}:latest"
    essential = true
    portMappings = [{ containerPort = 8083, protocol = "tcp" }]
    environment = concat(local.common_env, [
      { name = "SPRING_CLOUD_CONSUL_HOST",   value = "consul.${local.prefix}.local" },
      { name = "SPRING_DATASOURCE_URL",      value = "${local.mysql_url_base}/revcart_products?${local.mysql_opts}" },
      { name = "SPRING_DATASOURCE_USERNAME", value = "root" },
      { name = "SPRING_DATASOURCE_PASSWORD", value = var.db_password },
    ])
    logConfiguration = {
      logDriver = "awslogs"
      options = {
        awslogs-group         = aws_cloudwatch_log_group.main.name
        awslogs-region        = var.aws_region
        awslogs-stream-prefix = "product-service"
      }
    }
  }])
}

resource "aws_ecs_service" "product_service" {
  name            = "${local.prefix}-product-service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.product_service.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = var.private_subnet_ids
    security_groups  = [var.security_group_id]
    assign_public_ip = false
  }

  depends_on = [aws_ecs_service.consul]
}

# ── cart-service ──────────────────────────────────────────────────────────────
resource "aws_ecs_task_definition" "cart_service" {
  family                   = "${local.prefix}-cart-service"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "512"
  memory                   = "1024"
  execution_role_arn       = var.execution_role_arn
  task_role_arn            = var.task_role_arn

  container_definitions = jsonencode([{
    name      = "cart-service"
    image     = "${var.ecr_repositories["cart-service"]}:latest"
    essential = true
    portMappings = [{ containerPort = 8084, protocol = "tcp" }]
    environment = concat(local.common_env, [
      { name = "SPRING_CLOUD_CONSUL_HOST", value = "consul.${local.prefix}.local" },
      { name = "SPRING_REDIS_HOST",        value = var.redis_endpoint },
      { name = "SPRING_REDIS_PORT",        value = "6379" },
    ])
    logConfiguration = {
      logDriver = "awslogs"
      options = {
        awslogs-group         = aws_cloudwatch_log_group.main.name
        awslogs-region        = var.aws_region
        awslogs-stream-prefix = "cart-service"
      }
    }
  }])
}

resource "aws_ecs_service" "cart_service" {
  name            = "${local.prefix}-cart-service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.cart_service.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = var.private_subnet_ids
    security_groups  = [var.security_group_id]
    assign_public_ip = false
  }

  depends_on = [aws_ecs_service.consul]
}

# ── order-service ─────────────────────────────────────────────────────────────
resource "aws_ecs_task_definition" "order_service" {
  family                   = "${local.prefix}-order-service"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "512"
  memory                   = "1024"
  execution_role_arn       = var.execution_role_arn
  task_role_arn            = var.task_role_arn

  container_definitions = jsonencode([{
    name      = "order-service"
    image     = "${var.ecr_repositories["order-service"]}:latest"
    essential = true
    portMappings = [{ containerPort = 8085, protocol = "tcp" }]
    environment = concat(local.common_env, [
      { name = "SPRING_CLOUD_CONSUL_HOST",   value = "consul.${local.prefix}.local" },
      { name = "SPRING_DATASOURCE_URL",      value = "${local.mysql_url_base}/revcart_orders?${local.mysql_opts}" },
      { name = "SPRING_DATASOURCE_USERNAME", value = "root" },
      { name = "SPRING_DATASOURCE_PASSWORD", value = var.db_password },
    ])
    logConfiguration = {
      logDriver = "awslogs"
      options = {
        awslogs-group         = aws_cloudwatch_log_group.main.name
        awslogs-region        = var.aws_region
        awslogs-stream-prefix = "order-service"
      }
    }
  }])
}

resource "aws_ecs_service" "order_service" {
  name            = "${local.prefix}-order-service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.order_service.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = var.private_subnet_ids
    security_groups  = [var.security_group_id]
    assign_public_ip = false
  }

  depends_on = [aws_ecs_service.consul]
}

# ── payment-service ───────────────────────────────────────────────────────────
resource "aws_ecs_task_definition" "payment_service" {
  family                   = "${local.prefix}-payment-service"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "512"
  memory                   = "1024"
  execution_role_arn       = var.execution_role_arn
  task_role_arn            = var.task_role_arn

  container_definitions = jsonencode([{
    name      = "payment-service"
    image     = "${var.ecr_repositories["payment-service"]}:latest"
    essential = true
    portMappings = [{ containerPort = 8086, protocol = "tcp" }]
    environment = concat(local.common_env, [
      { name = "SPRING_CLOUD_CONSUL_HOST",   value = "consul.${local.prefix}.local" },
      { name = "SPRING_DATASOURCE_URL",      value = "${local.mysql_url_base}/revcart_payments?${local.mysql_opts}" },
      { name = "SPRING_DATASOURCE_USERNAME", value = "root" },
      { name = "SPRING_DATASOURCE_PASSWORD", value = var.db_password },
    ])
    logConfiguration = {
      logDriver = "awslogs"
      options = {
        awslogs-group         = aws_cloudwatch_log_group.main.name
        awslogs-region        = var.aws_region
        awslogs-stream-prefix = "payment-service"
      }
    }
  }])
}

resource "aws_ecs_service" "payment_service" {
  name            = "${local.prefix}-payment-service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.payment_service.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = var.private_subnet_ids
    security_groups  = [var.security_group_id]
    assign_public_ip = false
  }

  depends_on = [aws_ecs_service.consul]
}

# ── notification-service ──────────────────────────────────────────────────────
resource "aws_ecs_task_definition" "notification_service" {
  family                   = "${local.prefix}-notification-service"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "512"
  memory                   = "1024"
  execution_role_arn       = var.execution_role_arn
  task_role_arn            = var.task_role_arn

  container_definitions = jsonencode([{
    name      = "notification-service"
    image     = "${var.ecr_repositories["notification-service"]}:latest"
    essential = true
    portMappings = [{ containerPort = 8087, protocol = "tcp" }]
    environment = concat(local.common_env, [
      { name = "SPRING_CLOUD_CONSUL_HOST",         value = "consul.${local.prefix}.local" },
      { name = "SPRING_DATA_MONGODB_HOST",         value = var.docdb_endpoint },
      { name = "SPRING_DATA_MONGODB_PORT",         value = "27017" },
      { name = "SPRING_DATA_MONGODB_DATABASE",     value = "notification_db" },
      { name = "SPRING_DATA_MONGODB_USERNAME",     value = "admin" },
      { name = "SPRING_DATA_MONGODB_PASSWORD",     value = var.db_password },
      { name = "SPRING_MAIL_USERNAME",             value = var.mail_username },
      { name = "SPRING_MAIL_PASSWORD",             value = var.mail_password },
    ])
    logConfiguration = {
      logDriver = "awslogs"
      options = {
        awslogs-group         = aws_cloudwatch_log_group.main.name
        awslogs-region        = var.aws_region
        awslogs-stream-prefix = "notification-service"
      }
    }
  }])
}

resource "aws_ecs_service" "notification_service" {
  name            = "${local.prefix}-notification-service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.notification_service.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = var.private_subnet_ids
    security_groups  = [var.security_group_id]
    assign_public_ip = false
  }

  depends_on = [aws_ecs_service.consul]
}

# ── delivery-service ──────────────────────────────────────────────────────────
resource "aws_ecs_task_definition" "delivery_service" {
  family                   = "${local.prefix}-delivery-service"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "512"
  memory                   = "1024"
  execution_role_arn       = var.execution_role_arn
  task_role_arn            = var.task_role_arn

  container_definitions = jsonencode([{
    name      = "delivery-service"
    image     = "${var.ecr_repositories["delivery-service"]}:latest"
    essential = true
    portMappings = [{ containerPort = 8088, protocol = "tcp" }]
    environment = concat(local.common_env, [
      { name = "SPRING_CLOUD_CONSUL_HOST",   value = "consul.${local.prefix}.local" },
      { name = "SPRING_DATASOURCE_URL",      value = "${local.mysql_url_base}/revcart_delivery?${local.mysql_opts}" },
      { name = "SPRING_DATASOURCE_USERNAME", value = "root" },
      { name = "SPRING_DATASOURCE_PASSWORD", value = var.db_password },
      { name = "SPRING_MAIL_USERNAME",       value = var.mail_username },
      { name = "SPRING_MAIL_PASSWORD",       value = var.mail_password },
    ])
    logConfiguration = {
      logDriver = "awslogs"
      options = {
        awslogs-group         = aws_cloudwatch_log_group.main.name
        awslogs-region        = var.aws_region
        awslogs-stream-prefix = "delivery-service"
      }
    }
  }])
}

resource "aws_ecs_service" "delivery_service" {
  name            = "${local.prefix}-delivery-service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.delivery_service.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = var.private_subnet_ids
    security_groups  = [var.security_group_id]
    assign_public_ip = false
  }

  depends_on = [aws_ecs_service.consul]
}

# ── analytics-service ─────────────────────────────────────────────────────────
resource "aws_ecs_task_definition" "analytics_service" {
  family                   = "${local.prefix}-analytics-service"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "512"
  memory                   = "1024"
  execution_role_arn       = var.execution_role_arn
  task_role_arn            = var.task_role_arn

  container_definitions = jsonencode([{
    name      = "analytics-service"
    image     = "${var.ecr_repositories["analytics-service"]}:latest"
    essential = true
    portMappings = [{ containerPort = 8089, protocol = "tcp" }]
    environment = concat(local.common_env, [
      { name = "SPRING_CLOUD_CONSUL_HOST",     value = "consul.${local.prefix}.local" },
      { name = "SPRING_DATA_MONGODB_HOST",     value = var.docdb_endpoint },
      { name = "SPRING_DATA_MONGODB_PORT",     value = "27017" },
      { name = "SPRING_DATA_MONGODB_DATABASE", value = "analytics_db" },
      { name = "SPRING_DATA_MONGODB_USERNAME", value = "admin" },
      { name = "SPRING_DATA_MONGODB_PASSWORD", value = var.db_password },
    ])
    logConfiguration = {
      logDriver = "awslogs"
      options = {
        awslogs-group         = aws_cloudwatch_log_group.main.name
        awslogs-region        = var.aws_region
        awslogs-stream-prefix = "analytics-service"
      }
    }
  }])
}

resource "aws_ecs_service" "analytics_service" {
  name            = "${local.prefix}-analytics-service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.analytics_service.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = var.private_subnet_ids
    security_groups  = [var.security_group_id]
    assign_public_ip = false
  }

  depends_on = [aws_ecs_service.consul]
}

# ── admin-service ─────────────────────────────────────────────────────────────
resource "aws_ecs_task_definition" "admin_service" {
  family                   = "${local.prefix}-admin-service"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "512"
  memory                   = "1024"
  execution_role_arn       = var.execution_role_arn
  task_role_arn            = var.task_role_arn

  container_definitions = jsonencode([{
    name      = "admin-service"
    image     = "${var.ecr_repositories["admin-service"]}:latest"
    essential = true
    portMappings = [{ containerPort = 8090, protocol = "tcp" }]
    environment = concat(local.common_env, [
      { name = "SPRING_CLOUD_CONSUL_HOST",   value = "consul.${local.prefix}.local" },
      { name = "SPRING_DATASOURCE_URL",      value = "${local.mysql_url_base}/revcart_admin?${local.mysql_opts}" },
      { name = "SPRING_DATASOURCE_USERNAME", value = "root" },
      { name = "SPRING_DATASOURCE_PASSWORD", value = var.db_password },
    ])
    logConfiguration = {
      logDriver = "awslogs"
      options = {
        awslogs-group         = aws_cloudwatch_log_group.main.name
        awslogs-region        = var.aws_region
        awslogs-stream-prefix = "admin-service"
      }
    }
  }])
}

resource "aws_ecs_service" "admin_service" {
  name            = "${local.prefix}-admin-service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.admin_service.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = var.private_subnet_ids
    security_groups  = [var.security_group_id]
    assign_public_ip = false
  }

  depends_on = [aws_ecs_service.consul]
}

# ── frontend ──────────────────────────────────────────────────────────────────
resource "aws_ecs_task_definition" "frontend" {
  family                   = "${local.prefix}-frontend"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "256"
  memory                   = "512"
  execution_role_arn       = var.execution_role_arn
  task_role_arn            = var.task_role_arn

  container_definitions = jsonencode([{
    name      = "frontend"
    image     = "${var.ecr_repositories["frontend"]}:latest"
    essential = true
    portMappings = [{ containerPort = 80, protocol = "tcp" }]
    environment = [
      { name = "API_URL", value = "http://${var.alb_dns_name}" }
    ]
    logConfiguration = {
      logDriver = "awslogs"
      options = {
        awslogs-group         = aws_cloudwatch_log_group.main.name
        awslogs-region        = var.aws_region
        awslogs-stream-prefix = "frontend"
      }
    }
  }])
}

resource "aws_ecs_service" "frontend" {
  name            = "${local.prefix}-frontend"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.frontend.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = var.private_subnet_ids
    security_groups  = [var.security_group_id]
    assign_public_ip = false
  }

  load_balancer {
    target_group_arn = var.frontend_tg_arn
    container_name   = "frontend"
    container_port   = 80
  }
}
