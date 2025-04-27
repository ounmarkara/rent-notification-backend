# Rent Notification Backend

This is the backend for the Rent Notification web application, built with Spring Boot. It integrates Firebase Cloud Messaging (FCM) to send push notifications to tenants about rent due dates, uses PostgreSQL for data storage, Quartz for scheduling, and Socket.IO for real-time updates.

## Features
- Send push notifications to tenants via FCM.
- Schedule daily rent reminders using Quartz.
- Store tenant data (e.g., FCM tokens) in PostgreSQL.
- Real-time updates via Socket.IO (port 9092).

## Prerequisites
- **Java 17** or higher (JDK).
- **Maven** (for dependency management).
- **PostgreSQL** (version 13 or higher).
- **Node.js** (for Socket.IO, optional if not running frontend).
- **Firebase Project** (for FCM).
- **Git** (to clone the repository).

## Setup Instructions

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/rent-notification-backend.git
cd rent-notification-backend
