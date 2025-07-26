# Budget Buddy

## Demo

- [Watch the X-minute demo video](https://youtu.be/your-video-id)
    - A X minute walkthrough of signup, onboarding, dashboard, transaction logging, savings goals, visualisations, AI insights, and JWT security.

## Overview

**Budget Buddy** is a full-stack web application I developed as my portfolio project and CS50 final project. It empowers users to manage finances by logging transactions, setting savings goals, visualising spending patterns, and receiving AI-powered insights. This project highlights my skills in full-stack development, secure API design, and data visualisation.

## Features

- **Transaction Logging**: Manually add transactions or import via CSV (e.g., NAB bank data), with categories (groceries, transport) and dynamic balance updates.
- **Savings Goals**: Set goals (e.g., "Save $500 for a laptop") with deadlines, progress bars, goal statistics, and contribution tracking.
- **Visualisations**: Interactive pie charts and bar graphs via Recharts.js for spending, expense & income analysis.
- **Insights**: Personalised tips (e.g., "Reduce dining out to save $30/month") using the Gemini AI API.
- **REST API**: Secure, scalable API with Spring Boot, integrated with React Query for reliable frontend communication.
- **JWT Authentication:** Implements secure user login and session management using JSON Web Tokens (JWT), stored in HTTP-only cookies for enhanced security.
- **Database**: MySQL with Spring Data JPA for persistent storage.

## Concepts Learned:

- **Full-stack development** with React.js, Spring Boot, and Tailwind CSS, integrating modern frameworks cohesively.
- **Layered Architecture** using controller, service, and repository layers for modular, maintainable code.
- **Object-Oriented Programming (OOP)** principles like encapsulation and abstraction via JPA Repository and service polymorphism, allowing flexible data access implementations.
- **REST API design** with Spring Boot endpoints for efficient data exchange.
- **JWT security implementation**, leveraging existing solutions to enhance authentication without reinventing the wheel.

## Technologies

- **Backend**: Java, Spring Boot, Maven
- **Frontend**: React.js, Tailwind CSS, JavaScript
- **Database**: MySQL
- **Libraries**: Spring Data JPA, Recharts.js
- **Tools**: Git, Maven, VS Code, Intellij DEA
- **Testing**: JUnit for backend unit tests, GitHub Actions for CI/CD
- **AI Integration**: Gemini AI API

### Tech Stack Highlights

- **Spring Boot**: Manages RESTful services, JWT authentication, and database interactions.
- **React.js with React Query**: Delivers a responsive UI with efficient data handling.
- **Tailwind CSS**: Ensures a modern, responsive design.
- **MySQL with Spring Data JPA**: Supports scalable data management.
- **GitHub Actions**: Automates testing on code pushes.
- **Recharts.js**: Powers interactive visualisations.

### Development Process

- **Unit Testing**: Implemented JUnit tests for key backend classes (Controllers, Services & Repositories).
- **CI/CD**: Configured GitHub Actions to run tests on every push.
- **Real Data**: Imported real NAB transactions via CSV, with mockdata.csv for testing.

## Mock Transactions CSV

- Download [mockTransactions.csv](https://github.com/Shoxys/BudgetBuddy/blob/main/mockTransactions.csv) for sample transactions.
- Format: Date, Amount, Account Number, Empty, Transaction Type, Transaction Details, Balance, Category, Merchant Name (e.g., 05/01/2025, -100, 899000000, Debit, Groceries Purchase, 10000, Groceries, Fresh Foods Supermarket).
- **Note**: Based on NAB data headers, it may be different for other financial institutions.

## Future Plans

- **Production Server**: Deploy on AWS EC2 for scalability and public access.
- **Mobile App**: Develop a React Native version for on-the-go use.
- **AI Enhancements**: Integrate machine learning for predictive insights (e.g., overspending alerts).
- **Gamification**: Add rewards for hitting savings milestones.

## Author

- **Jack**, Computer Science Student at QUT

## License

- MIT License - Grants permission for free use, modification, and distribution, with the requirement to include the copyright and license notice.
