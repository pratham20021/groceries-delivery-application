locals {
  prefix = "${var.project}-${var.environment}"
}

resource "aws_db_subnet_group" "main" {
  name       = "${local.prefix}-rds-subnet-group"
  subnet_ids = var.subnet_ids
  tags       = { Name = "${local.prefix}-rds-subnet-group" }
}

resource "aws_db_parameter_group" "mysql8" {
  name   = "${local.prefix}-mysql8"
  family = "mysql8.0"

  parameter {
    name  = "character_set_server"
    value = "utf8mb4"
  }

  parameter {
    name  = "collation_server"
    value = "utf8mb4_unicode_ci"
  }

  tags = { Name = "${local.prefix}-mysql8-params" }
}

resource "aws_db_instance" "main" {
  identifier        = "${local.prefix}-mysql"
  engine            = "mysql"
  engine_version    = "8.0"
  instance_class    = var.instance_class
  allocated_storage = 20
  storage_type      = "gp3"
  storage_encrypted = true

  db_name  = "revcart"
  username = "root"
  password = var.db_password

  db_subnet_group_name   = aws_db_subnet_group.main.name
  vpc_security_group_ids = [var.security_group_id]
  parameter_group_name   = aws_db_parameter_group.mysql8.name

  multi_az            = false
  publicly_accessible = false
  skip_final_snapshot = true

  # Databases are created by each service on startup via createDatabaseIfNotExist
  # init script runs only on first boot if needed

  tags = { Name = "${local.prefix}-mysql" }
}
