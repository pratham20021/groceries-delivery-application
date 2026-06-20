variable "project"            { type = string }
variable "environment"        { type = string }
variable "vpc_id"             { type = string }
variable "private_subnet_ids" { type = list(string) }
variable "public_subnet_ids"  { type = list(string) }
variable "execution_role_arn" { type = string }
variable "task_role_arn"      { type = string }
variable "security_group_id"  { type = string }
variable "alb_sg_id"          { type = string }
variable "alb_dns_name"       { type = string }
variable "api_gateway_tg_arn" { type = string }
variable "frontend_tg_arn"    { type = string }
variable "ecr_repositories"   { type = map(string) }
variable "mysql_endpoint"     { type = string }
variable "redis_endpoint"     { type = string }
variable "docdb_endpoint"     { type = string }
variable "db_password" {
  type      = string
  sensitive = true
}
variable "mail_username" {
  type      = string
  sensitive = true
}
variable "mail_password" {
  type      = string
  sensitive = true
}
variable "google_client_id" {
  type      = string
  sensitive = true
}
variable "google_client_secret" {
  type      = string
  sensitive = true
}
variable "aws_region" { type = string }
