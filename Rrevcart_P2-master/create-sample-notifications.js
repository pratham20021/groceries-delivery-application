// MongoDB script to create sample notifications
db = db.getSiblingDB('revcart_notifications');

db.notifications.insertMany([
  {
    userId: 1,
    type: "ORDER",
    title: "Order Confirmed",
    message: "Your order #12345 has been confirmed and is being processed",
    read: false,
    createdAt: new Date()
  },
  {
    userId: 1,
    type: "ORDER",
    title: "Order Packed",
    message: "Your order #12345 has been packed and ready for dispatch",
    read: false,
    createdAt: new Date()
  },
  {
    userId: 1,
    type: "ORDER",
    title: "Out for Delivery",
    message: "Your order #12345 is out for delivery and will reach you soon",
    read: false,
    createdAt: new Date()
  },
  {
    userId: 1,
    type: "ORDER",
    title: "Order Delivered",
    message: "Your order #12345 has been delivered successfully",
    read: false,
    createdAt: new Date()
  }
]);

print("Sample notifications created successfully!");
