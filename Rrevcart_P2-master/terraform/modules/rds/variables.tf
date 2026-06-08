variable "project"           { type = string }
variable "environment"       { type = string }
variable "subnet_ids"        { type = list(string) }
variable "security_group_id" { type = string }
variable "db_password"       { type = string; sensitive = true }
variable "instance_class"    { type = string; default = "db.t3.medium" }
