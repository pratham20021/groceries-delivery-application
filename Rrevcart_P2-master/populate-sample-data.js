// Sample data population script
const axios = require('axios');

const API_BASE = 'http://localhost:8080/api';

async function populateSampleData() {
    console.log('Populating sample data...');
    
    try {
        // Create sample orders
        const sampleOrders = [
            {
                userId: 1,
                orderNumber: 'ORD-001',
                totalAmount: 299.99,
                status: 'PENDING',
                couponCode: 'WELCOME10',
                discountAmount: 30,
                orderItems: [
                    {
                        productName: 'Wireless Headphones',
                        quantity: 1,
                        price: 199.99
                    },
                    {
                        productName: 'Phone Case',
                        quantity: 2,
                        price: 65.00
                    }
                ]
            },
            {
                userId: 1,
                orderNumber: 'ORD-002',
                totalAmount: 149.99,
                status: 'COMPLETED',
                orderItems: [
                    {
                        productName: 'Bluetooth Speaker',
                        quantity: 1,
                        price: 149.99
                    }
                ]
            }
        ];
        
        for (const order of sampleOrders) {
            try {
                const response = await axios.post(`${API_BASE}/orders`, order);
                console.log('Created order:', response.data);
            } catch (err) {
                console.error('Failed to create order:', err.message);
            }
        }
        
        // Create sample notifications
        const sampleNotifications = [
            {
                userId: 1,
                type: 'ORDER_PLACED',
                title: 'Order Placed Successfully',
                message: 'Your order #ORD-001 has been placed successfully.'
            },
            {
                userId: 1,
                type: 'ORDER_PACKED',
                title: 'Order Packed',
                message: 'Your order #ORD-001 has been packed and ready for shipping.'
            },
            {
                userId: 1,
                type: 'PAYMENT_SUCCESS',
                title: 'Payment Successful',
                message: 'Payment of ₹299.99 has been processed successfully.'
            }
        ];
        
        for (const notification of sampleNotifications) {
            try {
                const response = await axios.post(`${API_BASE}/notifications/send`, notification);
                console.log('Created notification:', response.data);
            } catch (err) {
                console.error('Failed to create notification:', err.message);
            }
        }
        
        console.log('Sample data population completed!');
        
    } catch (error) {
        console.error('Error populating sample data:', error.message);
    }
}

// Run if called directly
if (require.main === module) {
    populateSampleData();
}

module.exports = { populateSampleData };