terraform {
  required_version = ">= 1.6"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }

  # S3 remote state — Jenkins uses this to share state across builds
  backend "s3" {
    bucket         = "revcart-terraform-state"
    key            = "revcart/terraform.tfstate"
    region         = "us-east-1"
    encrypt        = true
    dynamodb_table = "revcart-terraform-locks"
  }
}

provider "aws" {
  region = var.aws_region
}

# ── VPC ──────────────────────────────────────────────────────────────────────
module "vpc" {
  source       = "./modules/vpc"
  project      = var.project
  environment  = var.environment
  vpc_cidr     = var.vpc_cidr
  azs          = var.azs
}

# ── Security Groups ───────────────────────────────────────────────────────────
module "security_groups" {
  source      = "./modules/security_groups"
  project     = var.project
  environment = var.environment
  vpc_id      = module.vpc.vpc_id
  vpc_cidr    = var.vpc_cidr
}

# ── IAM ───────────────────────────────────────────────────────────────────────
module "iam" {
  source      = "./modules/iam"
  project     = var.project
  environment = var.environment
}

# ── ECR ───────────────────────────────────────────────────────────────────────
module "ecr" {
  source      = "./modules/ecr"
  project     = var.project
  environment = var.environment
  services    = var.services
}

# ── RDS MySQL ─────────────────────────────────────────────────────────────────
module "rds" {
  source            = "./modules/rds"
  project           = var.project
  environment       = var.environment
  subnet_ids        = module.vpc.private_subnet_ids
  security_group_id = module.security_groups.rds_sg_id
  db_password       = var.db_password
  instance_class    = var.rds_instance_class
}

# ── ElastiCache Redis ─────────────────────────────────────────────────────────
module "elasticache" {
  source            = "./modules/elasticache"
  project           = var.project
  environment       = var.environment
  subnet_ids        = module.vpc.private_subnet_ids
  security_group_id = module.security_groups.redis_sg_id
  node_type         = var.redis_node_type
}

# ── DocumentDB (MongoDB-compatible) ───────────────────────────────────────────
module "docdb" {
  source            = "./modules/docdb"
  project           = var.project
  environment       = var.environment
  subnet_ids        = module.vpc.private_subnet_ids
  security_group_id = module.security_groups.docdb_sg_id
  db_password       = var.db_password
  instance_class    = var.docdb_instance_class
}

# ── ALB ───────────────────────────────────────────────────────────────────────
module "alb" {
  source            = "./modules/alb"
  project           = var.project
  environment       = var.environment
  vpc_id            = module.vpc.vpc_id
  public_subnet_ids = module.vpc.public_subnet_ids
  security_group_id = module.security_groups.alb_sg_id
  certificate_arn   = var.certificate_arn
}

# ── ECS ───────────────────────────────────────────────────────────────────────
module "ecs" {
  source = "./modules/ecs"

  project     = var.project
  environment = var.environment

  vpc_id             = module.vpc.vpc_id
  private_subnet_ids = module.vpc.private_subnet_ids
  public_subnet_ids  = module.vpc.public_subnet_ids

  execution_role_arn = module.iam.ecs_execution_role_arn
  task_role_arn      = module.iam.ecs_task_role_arn

  security_group_id = module.security_groups.ecs_sg_id
  alb_sg_id         = module.security_groups.alb_sg_id

  alb_dns_name          = module.alb.alb_dns_name
  api_gateway_tg_arn    = module.alb.api_gateway_tg_arn
  frontend_tg_arn       = module.alb.frontend_tg_arn

  ecr_repositories = module.ecr.repository_urls

  # Infrastructure endpoints
  mysql_endpoint    = module.rds.endpoint
  redis_endpoint    = module.elasticache.endpoint
  docdb_endpoint    = module.docdb.endpoint

  db_password       = var.db_password
  mail_username     = var.mail_username
  mail_password     = var.mail_password
  google_client_id  = var.google_client_id
  google_client_secret = var.google_client_secret

  aws_region  = var.aws_region
}
