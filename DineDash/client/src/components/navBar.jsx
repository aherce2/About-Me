import React, { useState } from 'react';
import Button from 'react-bootstrap/Button';
import Container from 'react-bootstrap/Container';
import Form from 'react-bootstrap/Form';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import { NavLink, useLocation, useNavigate } from 'react-router-dom';
import Filters from './filters';
import axios from 'axios';
import "./navBar.css"

const NavigationBar = ({ RestaurantFiltersApplied, setRestaurantFiltersApplied, ActivePage, setActivePage, onLogout, onDelete }) => {
  const location = useLocation();
  const navigate = useNavigate();
  const [query, setQuery] = useState('');
  const [error, setError] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  const formatFilters = (filters) => {
    return Object.keys(filters)
      .map((filterDict) => {
        const filterTypes = filters[filterDict];
        return Object.keys(filterTypes)
          .map((filterType) => {
            const filterConfig = filterTypes[filterType];
            if (Array.isArray(filterConfig)) {
              return `${filterType}: ${filterConfig.join(', ')}`;
            } else if (
              typeof filterConfig === 'object' &&
              filterConfig.min !== undefined &&
              filterConfig.max !== undefined
            ) {
              return `${filterType}: ${filterConfig.min} - ${filterConfig.max}`;
            } else {
              return `${filterType}: ${filterConfig}`;
            }
          })
          .join(', ');
      })
      .join('; ');
  };

  const handleInputChange = (e) => {
    setQuery(e.target.value);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    console.log(Object.values(filters).every(filterDict => Object.keys(filterDict).length === 0));
    if (Object.values(filters).every(filterDict => Object.keys(filterDict).length === 0)) {
      setRestaurantFiltersApplied(false);
      localStorage.removeItem('filteredResults');
      console.log('No filters were added');
      return; // Exit the function early if filters are empty
    }
    setIsLoading(true);

    try {
      const response = await axios.post('http://localhost:5000/api/searchrestaurants', {
        query: query,
      });

      const data = await response.data;
      console.log('Data obtained from search query', data);

      if (data.success && data.restaurants.length > 0) {
        const formattedResults = {
          message: `Showing Results for Search: ${query}`,
          query: query,
          restaurants: data.restaurants,
        };
        localStorage.setItem('searchResults', JSON.stringify(formattedResults));
        console.log('Stored search result to Local Storage');
        setRestaurantFiltersApplied(false);
        if (location.pathname !== '/restaurants') {
          navigate('/restaurants');
        }
      } else {
        localStorage.setItem('searchResults', JSON.stringify({ restaurants: [], message: 'No results found.' }));
      }
      setQuery('');
      window.dispatchEvent(new Event('storage'));
    } catch (error) {
      console.error('Error:', error);
      setError(error.message || 'An unknown error occurred');
    } finally {
      setIsLoading(false);
    }
  };

  const handleFiltersChange = async (filters) => {
    console.log(Object.values(filters).every(filterDict => Object.keys(filterDict).length === 0));
    if (Object.values(filters).every(filterDict => Object.keys(filterDict).length === 0)) {
    setRestaurantFiltersApplied(false);
    localStorage.removeItem('filteredResults');
    console.log('No filters were added');
    return; // Exit the function early if filters are empty
  }

    console.log('Initial state of Restaurant Filters Applied:', RestaurantFiltersApplied);

    console.log('Filters Applied:', filters);

    const userZip = localStorage.getItem('userZip');
    setIsLoading(true);

    try {
      const response = await axios.post('http://localhost:5000/api/restaurants/filter', {
        filters: filters,
        userZip: userZip,
      });

      const data = await response.data;
      console.log('Data obtained from filter query', data);

      if (data.success && data.data.length > 0) {
        setRestaurantFiltersApplied(true);
        console.log(data.data);

        const filtersMessage = formatFilters(filters);
        console.log(`Filtered Results: ${filtersMessage}`);

        const formattedResults = {
          message: (`Showing Filtered Results: ${filtersMessage}`),
          filters: filters,
          restaurants: data.data
        };

        localStorage.setItem('filteredResults', JSON.stringify(formattedResults));
        console.log('Stored filtered result to Local Storage');

        if (location.pathname !== '/restaurants') {
          navigate('/restaurants');
        }
      } else {
        setRestaurantFiltersApplied(true);
        const filtersMessage = formatFilters(filters);
        console.log(`Filtered Results Error: ${filtersMessage}`);
        const formattedResults = {
          message: `No Results for Filters: ${filtersMessage}`,
          filters: filters,
          restaurants: data.data,
        };

        localStorage.setItem('filteredResults', JSON.stringify(formattedResults));
      }

      window.dispatchEvent(new Event('storage'));
    } catch (error) {
      console.error('Error:', error);
      setError(error.message || 'An unknown error occurred');
    } finally {
      setIsLoading(false);
    }
  };

  const isOrdersPage = location.pathname === '/orders';
  const userId = localStorage.getItem('userId');
  const email = localStorage.getItem('email');

  return (
    <div>
      <Navbar expand="lg" className="navbar">
        <Container fluid className="navbar-container">
          <Navbar.Brand className="navbar-brand">DineDash</Navbar.Brand>
          <Navbar.Toggle className="navbar-toggler" />
          <Navbar.Collapse id="navbarScroll">
            <Nav className="justify-content-end flex-grow-1 pe-3">
              <Nav.Link as={NavLink} to="/restaurants" className={`nav-link ${!ActivePage ? 'inactive' : ''}`} onClick={() => setActivePage(true)}>Home</Nav.Link>
              <Nav.Link as={NavLink} to="/orders" className="nav-link">Orders</Nav.Link>
            </Nav>
            <Nav>
              <div style={{ opacity: isOrdersPage ? 0.5 : 1, pointerEvents: isOrdersPage ? 'none' : 'auto' }}>
                <Filters activePage={ActivePage} setRestaurantFiltersApplied={setRestaurantFiltersApplied} onFiltersChange={handleFiltersChange} />
              </div>
            </Nav>
            <Form className="d-flex ms-3" onSubmit={handleSubmit}>
              <Form.Control type="text" name="search" placeholder="Search" className="me-2" label="Search" value={query} onChange={handleInputChange} />
              <Button variant="outline-secondary" className="btn-outline" type='submit'>Search</Button>
            </Form>
            <div className="ms-auto">
              <Nav className="ms-auto">
                <Nav.Link onClick={onLogout} className="nav-link-logout">Logout</Nav.Link>
              </Nav>
            </div>
          </Navbar.Collapse>
        </Container>
      </Navbar>
      {userId && email && (
        <div className="user-info">
          <Container fluid>
            <p className="user-info-text">Logged in as User: {userId}</p>
          </Container>
        </div>
      )}
      <div className="bottom-right">
        <Button onClick={onDelete} className="delete-button">Delete Account</Button>
      </div>
      {isLoading && (
        <div className="loading-overlay">
        <p className="loading-text">Please wait while we search the database...</p>
      </div>
      )}
    </div>
  );
}

export default NavigationBar;
