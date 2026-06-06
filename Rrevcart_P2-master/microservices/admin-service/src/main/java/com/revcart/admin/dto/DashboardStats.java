package com.revcart.admin.dto;

public class DashboardStats {
    private long totalProducts;
    private long totalOrders;
    private long totalUsers;
    private double totalRevenue;
    
    public DashboardStats() {}
    
    public DashboardStats(long totalProducts, long totalOrders, long totalUsers, double totalRevenue) {
        this.totalProducts = totalProducts;
        this.totalOrders = totalOrders;
        this.totalUsers = totalUsers;
        this.totalRevenue = totalRevenue;
    }
    
    public long getTotalProducts() { return totalProducts; }
    public void setTotalProducts(long totalProducts) { this.totalProducts = totalProducts; }
    
    public long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(long totalOrders) { this.totalOrders = totalOrders; }
    
    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    
    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
}
