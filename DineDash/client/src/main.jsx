/*To add for organization:
  assests/ : folder for static assests (imaegs, icons, etc)

  Components/ : Reusable React Components
    loginForm.jsx: Login form component
      create component for login form
      Include message/button for User not found / Create account / Login
    NavBar.jsx: Navigation bar compoent
      navigation bar optional for different pages
      Access actions like logout
    SearchBar.jsx: search bar/filters 
      components for search bar and filter application
      use onSearch function passed as prop
    UserCard.jsx: display user info
      component to display user information
    RestaurantCard.jsx: restaurant display card
      display restaurant information after user applies searches and/or filters
    RestaurantItemsCard.jsx: restaurant menu items card
      Display menuitems and details 
      Display add to cart button
    cart.jsx: cart display/management
      display and manage cart for user order
      include clearing out cart
    FilterDropdown.jsx: Component for adding filters
      display the various filters (queries)
    OrderHistory.jsx
      displays past orders for a user with ability to delete it


  pages/: React pages
    LoginPage.jsx: Page with login Form
      display login form
    HomePage.jsx: Page after successful login
      page shown after successful login
      handle search and filter functionality 
    Restaurant.jsx: Page for restaurant items & shopping cart
      Page showing restaurant menu items
    OrderPage.jsx
      page to view and manage past orders


  apps.css: global styles
  app.jsx: main app component with routing
    main component for routing: include all pages
  index.css: global styles
  main.jsx: Entry point for react
  api.js: API calls to Flask Server
    API utility functions for interacting with Flask backend
*/


import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'
import './index.css'
import 'bootstrap/dist/css/bootstrap.min.css'; // Import Bootstrap CSS
import './global.css';

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
)

/* Advanted Database Features (in api.js)
Transactions: user places order that consists of multiple items. ensure entire order and items are inserted into the database as single transaction
  Frontend: front sends request with order details to backend. backend executes and returns sucess or failure 
Stored procedures: implement the filters/search applied
  Frontend: requests database and backend calls stored procedure
  SQL:
    parameters: accept parameters corresponding to possible filters (zipcode, rating range, cuisine types, etc.)
    Dynamic query: construct based on paramters provided
      define base query string, based on what filters are provided (not NULL) it concatenates the appropriate condition
    Return: execute constructed query and return result
    Frontend: allow fronted to send filter paramters based on user intput, and handle the response to update UI
  API Endpoint:
    get all the arguments
    call stored procedure with parameters
    store result
    close cursor
    convert to JSON serializable format
    return jsonify(JSON format)
  FRONT: search bar
    useState for all inputs
Triggers: update restaurant average rating when new rating inserted
  would require extra front/backend handling to implement
Constraints: ensure data integrity with foreign keys
  frontend: inform users of any constraint violations (duplicate emails, invalid filters, no results)


*/