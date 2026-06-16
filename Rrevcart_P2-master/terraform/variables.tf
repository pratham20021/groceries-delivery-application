variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "ap-south-1"
}

variable "project" {
  description = "Project name"
  type        = string
  default     = "revcart"
}

variable "environment" {
  description = "Deployment environment"
  type        = string
  default     = "prod"
}

variable "vpc_cidr" {
  description = "VPC CIDR block"
  type        = string
  default     = "10.0.0.0/16"
}

variable "azs" {
  description = "Availability zones"
  type        = list(string)
  default     = ["ap-south-1a", "ap-south-1b"]
}

variable "services" {
  description = "List of microservice names"
  type        = list(string)
  default = [
    "api-gateway",
    "auth-service",
    "user-service",
    "product-service",
    "cart-service",
    "order-service",
    "payment-service",
    "notification-service",
    "delivery-service",
    "analytics-service",
    "admin-service",
    "frontend"
  ]
}

variable "db_password" {
  description = "Password for RDS and DocumentDB"
  type        = string
  sensitive   = true
}

variable "mail_username" {
  description = "Gmail username for notifications"
  type        = string
  sensitive   = true
}

variable "mail_password" {
  description = "Gmail app password"
  type        = string
  sensitive   = true
}

variable "google_client_id" {
  description = "Google OAuth2 client ID"
  type        = string
  sensitive   = true
}

variable "google_client_secret" {
  description = "Google OAuth2 client secret"
  type        = string
  sensitive   = true
}

variable "rds_instance_class" {
  description = "RDS instance class"
  type        = string
  default     = "db.t3.medium"
}

variable "redis_node_type" {
  description = "ElastiCache node type"
  type        = string
  default     = "cache.t3.micro"
}

variable "docdb_instance_class" {
  description = "DocumentDB instance class"
  type        = string
  default     = "db.t3.medium"
}

variable "certificate_arn" {
  description = "ACM certificate ARN for HTTPS (leave empty for HTTP only)"
  type        = string
  default     = ""
}
