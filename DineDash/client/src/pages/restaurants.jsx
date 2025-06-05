// Restaurants.jsx
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Card from 'react-bootstrap/Card';
import Col from 'react-bootstrap/Col';
import axios from 'axios';
import Row from 'react-bootstrap/Row';
import './restaurants.css';
import Container from 'react-bootstrap/Container';
import Button from 'react-bootstrap/Button';
import DropdownMenu from '../components/dropdown.jsx';

const Restaurants = ({ RestaurantFiltersApplied, setRestaurantFiltersApplied, setActivePage }) => {
  const [restaurants, setRestaurants] = useState([]);
  const [message, setMessage] = useState('');
  const [showClearButton, setShowClearButton] = useState(false);
  const [showDropdown, setShowDropdown] = useState(false);
  const navigate = useNavigate();

  const automaticQueries = [
    'Most popular Restaurant',
    'Most popular Menu Item',
    'Top 50 Rated Restaurants',
    'Restaurants above average Rating of all restaurants',
    'Restaurants above average Rating in Area',
    'Least expensive Items at restaurant',
    'Least popular Menu Items',
    'Most expensive Menu Items'
  ];

  useEffect(() => {
    const loadRestaurants = () => {
      if (RestaurantFiltersApplied) {
        const storedFilteredRestaurants = JSON.parse(localStorage.getItem('filteredResults'));

        if (storedFilteredRestaurants && storedFilteredRestaurants.restaurants && storedFilteredRestaurants.restaurants.length > 0) {
          setRestaurants(storedFilteredRestaurants.restaurants);
          setMessage(storedFilteredRestaurants.message || 'Filtered results found.');
          setShowClearButton(true);
          setShowDropdown(true);
        } else {
          setRestaurants([]);
          setShowDropdown(false);
          setMessage('No results matching the applied filters.');
          setShowClearButton(false);
        }
      } else {
        const storedSearchResults = JSON.parse(localStorage.getItem('searchResults'));

        if (storedSearchResults && storedSearchResults.restaurants) {
          setRestaurants(storedSearchResults.restaurants);
          setMessage(storedSearchResults.message || 'Search results found.');
          setShowClearButton(true);
          setShowDropdown(true);
        } else {
          const storedRestaurants = JSON.parse(localStorage.getItem('default_restaurants'));

          if (storedRestaurants && storedRestaurants.restaurants) {
            setRestaurants(storedRestaurants.restaurants);
            setMessage(storedRestaurants.message || 'Showing default restaurants.');
            setShowClearButton(false);
            setShowDropdown(true);
          } else {
            setRestaurants([]);
            setShowDropdown(false);
            setMessage('No Restaurant data found.');
            setShowClearButton(false);
          }
        }
      }
    };

    loadRestaurants();

    const handleStorageChange = () => {
      loadRestaurants();
    };

    window.addEventListener('storage', handleStorageChange);

    return () => {
      window.removeEventListener('storage', handleStorageChange);
    };
  }, [RestaurantFiltersApplied]);

  const fetchMenu = async (restaurantId) => {
    try {
      const cachedMenu = JSON.parse(localStorage.getItem(`menu_${restaurantId}`));
      if (cachedMenu) {
        console.log('Menu data retrieved from local storage:', cachedMenu);
        return cachedMenu;
      }
      console.log('Fetching Menu data from Database');
      const response = await axios.get(`http://localhost:5000/api/menuItems/${restaurantId}`);
      console.log('Restaurant Information', response.data);
      const menu = response.data;

      if (menu.success) {
        const menuData = {
          restaurantId,
          restaurantName: restaurants.find(r => r.RestaurantID === restaurantId).RestaurantName,
          menuItems: menu.menu
        };
        localStorage.setItem(`menu_${restaurantId}`, JSON.stringify(menuData));
        console.log('Restaurant Menu data fetched and stored in localStorage:', menuData);
        return menuData;
      } else {
        console.error('Failed to fetch menu items');
        return {};
      }
    } catch (error) {
      console.error('Error fetching Restaurant Menu data:', error);
      return {};
    }
  };

  const handleMenuItems = async (restaurantId) => {
    if (setActivePage) {
      setActivePage(false);
    }    
    if (restaurantId) {
      const menuItems = await fetchMenu(restaurantId);
      console.log(menuItems)
      if (Object.keys(menuItems).length === 0) {
        setRestaurants([]); // Clear restaurants if no results found
        setMessage('No Menu Items for Restaurant');
      } else {
        console.log("Fetched Menu Items for Restaurant. Navigating to Menu Page");
        navigate(`/menuItems/${restaurantId}`);
      }
    }
  };


  const noResults = () => {
    
    setShowDropdown(false);
    setRestaurantFiltersApplied(false);
    setActivePage(true);
    setShowClearButton(false);
    localStorage.removeItem('filteredResults');
    const storedRestaurants = JSON.parse(localStorage.getItem('default_restaurants'));
    if (storedRestaurants && storedRestaurants.restaurants) {
      setRestaurants(storedRestaurants.restaurants);
      setMessage('Showing Restaurants Near You!');
      setShowDropdown(true); // Show dropdown for default restaurants
    } else {
      setRestaurants([]);
      setMessage('No Restaurant data found.');
      console.error('No default restaurants data found in local storage.');
    }
  };

  const clearSearchResults = () => {
    setShowDropdown(false);
    localStorage.removeItem('searchResults');
    noResults();
  };

  const handleDropdownSelect = async (query, idx) => {
    console.log(`Executing query: ${query} with idx: ${idx}`);

    navigate(`/queryResults/${idx}`);


  };

  return (
    <Container fluid>
      <div className="restaurants-container">
        <div className="dropdown-wrapper">
          {message && (
            <div className="message-container">
              <h3 className="message-title">{message}</h3>
            </div>
          )}
          {restaurants.length > 0 && showDropdown && (
            <div className="dropdown-container">
              <DropdownMenu
                options={automaticQueries}
                onSelect={handleDropdownSelect}
              />
            </div>
          )}
        </div>
        {message && !restaurants.length && (
          <div className="message-button">
            <Button onClick={noResults} className="btn-show-default">Go Back</Button>
          </div>
        )}
        {showClearButton && (
          <div className="clear-search-button">
            <Button onClick={clearSearchResults} className="btn-clear-search">Clear Search</Button>
          </div>
        )}
        {restaurants.length > 0 && (
          <Row className="g-4">
            {restaurants.map((restaurant, idx) => (
              <Col
                key={idx}
                className={`mb-4 ${restaurants.length <= 1 ? 'col-12' : restaurants.length === 2 ? 'col-6' : 'col-12 col-md-6 col-lg-4'}`}
              >
                <Card
                  className="restaurants-card-custom clickable mb-4"
                  onClick={() => handleMenuItems(restaurant.RestaurantID)}
                >
                  <Card.Header className="restaurant-card-header">
                    <div className="restaurant-info restaurant-name">{restaurant.RestaurantName}</div>
                  </Card.Header>
                  <Card.Body className="card-body">
                    <Card.Title className="restaurant-id">{`Restaurant ID: ${restaurant.RestaurantID}`}</Card.Title>
                    <div className="restaurant-details">
                      <div className="item-row"><span className="restaurant-attributes-name">Category:</span><span>{restaurant.Category}</span></div>
                      <div className="item-row"><span className="restaurant-attributes-name">Price Range:</span> <span>{restaurant.PriceRange}</span></div>
                      <div className="item-row"><span className="restaurant-attributes-name">Average Score:</span><span>{restaurant.Score}</span></div>
                      <div className="item-row"> <span className="restaurant-attributes-name">Total Ratings:</span><span>{restaurant.Ratings}</span></div>
                      <div className="item-row"><span className="restaurant-attributes-name">Zipcode:</span><span>{restaurant.Zipcode}</span></div>
                    </div>
                  </Card.Body>
                </Card>
              </Col>
            ))}
          </Row>
        )}
      </div>
    </Container>
  );
};

export default Restaurants;
