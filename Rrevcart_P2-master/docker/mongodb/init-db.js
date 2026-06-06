// Initialize MongoDB databases
db = db.getSiblingDB('revcart_notifications');
db.createCollection('notifications');

db = db.getSiblingDB('revcart_analytics');
db.createCollection('analytics');

print('MongoDB databases initialized successfully');
