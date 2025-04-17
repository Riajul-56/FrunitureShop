# Java Swing Furniture Shop POS System

A simple and colorful Point of Sale (POS) system built with Java Swing for a furniture shop. It includes user login/registration with roles (`admin` and `customer`), cart management, product search, receipt generation, dark mode, printing, and sales reports.

## Features

- 🔐 **Login/Register System**

  - Users can log in or register with a role (`admin` or `customer`)
  - Passwords are stored securely using SHA-256 hashing

- 🛒 **Product Cart**

  - Add predefined products: Chair, Table, Sofa, Bed
  - View items in a cart with prices
  - Display running total

- 🎁 **Discount**

  - Admins can apply a 10% discount to the total

- 🔍 **Product Search**

  - Search for products by name and add them to the cart

- 🧾 **Receipt Generation**

  - Generate and save a receipt to a text file
  - Receipts include username, date, product list, and total

- 📁 **Receipt History**

  - View previously saved receipts

- 📊 **Sales Report (Admin Only)**

  - Displays number of receipts and total sales

- 🌙 **Dark Mode**

  - Toggle between light and dark themes

- 🖨️ **Print Receipt**

  - Print the contents of the cart table

- 👥 **User Role Support**
  - Admin: Full access
  - Customer: Limited features (no discount or sales report)

## GUI Design

- Colorful interface with hover effects
- Modern, responsive layout using `GridLayout` and `BorderLayout`

## File Structure

FurniturePOS/ ├── POS.java # Main application class ├── receipt_username.txt # Automatically created per user for receipts ├── users.txt # Stores usernames, hashed passwords, and roles ├── README.md # Project documentation

## How to Run

1. Compile the Java file:
   ```bash
   javac POS.java
    java POS
   
# Role Select 
![logo](https://github.com/Riajul-56/FrunitureShop/blob/main/Role.png)
# Login 
![logo](https://github.com/Riajul-56/FrunitureShop/blob/main/login.png)
# Admin Pannel
![logo](https://github.com/Riajul-56/FurnitureShop/blob/main/admin.png)
# Customer Pannel
![logo](https://github.com/Riajul-56/FurnitureShop/blob/main/customer.png)


