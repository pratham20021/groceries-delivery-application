output "alb_dns_name"        { value = aws_lb.main.dns_name }
output "alb_arn"             { value = aws_lb.main.arn }
output "api_gateway_tg_arn"  { value = aws_lb_target_group.api_gateway.arn }
output "frontend_tg_arn"     { value = aws_lb_target_group.frontend.arn }
