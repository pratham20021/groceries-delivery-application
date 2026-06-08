locals {
  prefix = "${var.project}-${var.environment}"
}

resource "aws_docdb_subnet_group" "main" {
  name       = "${local.prefix}-docdb-subnet-group"
  subnet_ids = var.subnet_ids
  tags       = { Name = "${local.prefix}-docdb-subnet-group" }
}

resource "aws_docdb_cluster_parameter_group" "main" {
  family = "docdb5.0"
  name   = "${local.prefix}-docdb-params"

  parameter {
    name  = "tls"
    value = "disabled"
  }

  tags = { Name = "${local.prefix}-docdb-params" }
}

resource "aws_docdb_cluster" "main" {
  cluster_identifier              = "${local.prefix}-docdb"
  engine                          = "docdb"
  master_username                 = "admin"
  master_password                 = var.db_password
  db_subnet_group_name            = aws_docdb_subnet_group.main.name
  vpc_security_group_ids          = [var.security_group_id]
  db_cluster_parameter_group_name = aws_docdb_cluster_parameter_group.main.name
  skip_final_snapshot             = true
  storage_encrypted               = true

  tags = { Name = "${local.prefix}-docdb" }
}

resource "aws_docdb_cluster_instance" "main" {
  count              = 1
  identifier         = "${local.prefix}-docdb-${count.index}"
  cluster_identifier = aws_docdb_cluster.main.id
  instance_class     = var.instance_class

  tags = { Name = "${local.prefix}-docdb-${count.index}" }
}
