locals {
  prefix = "${var.project}-${var.environment}"
}

resource "aws_elasticache_subnet_group" "main" {
  name       = "${local.prefix}-redis-subnet-group"
  subnet_ids = var.subnet_ids
  tags       = { Name = "${local.prefix}-redis-subnet-group" }
}

resource "aws_elasticache_cluster" "main" {
  cluster_id           = "${local.prefix}-redis"
  engine               = "redis"
  node_type            = var.node_type
  num_cache_nodes      = 1
  parameter_group_name = "default.redis7"
  engine_version       = "7.0"
  port                 = 6379
  subnet_group_name    = aws_elasticache_subnet_group.main.name
  security_group_ids   = [var.security_group_id]

  tags = { Name = "${local.prefix}-redis" }
}
