import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import Card from 'react-bootstrap/Card';
import Col from 'react-bootstrap/Col';
import Row from 'react-bootstrap/Row';
import Container from 'react-bootstrap/Container';
import Button from 'react-bootstrap/Button';
import './queryResults.css';

const QueryResults = () => {
  const { queryId } = useParams();
  const navigate = useNavigate();
  const [results, setResults] = useState([]);
  const [error, setError] = useState(null);
  const [queryTitle, setQueryTitle] = useState('');

  const automaticQueries = [
    'Most popular Restaurant',
    'Most popular Menu Item',
    'Top 50 Rated Restaurants',
    'Restaurants above average Rating of all restaurants',
    'Restaurants above average Rating in Area',
    'Least expensive Items at restaurant',
    'Least popular Menu Items',
    'Most expensive Menu Items',
  ];

  useEffect(() => {
    const fetchQueryResults = async () => {
      console.log('Fetching data for query ID:', queryId); // Debug log for query ID
      const userZip = localStorage.getItem('userZip');
      console.log('Retrieved zipcode:', userZip); // Debug log for zipcode
      const idx = queryId;
      try {
        const response = await axios.get(`http://localhost:5000/api/query/${idx}`, {
          params: { userZip },
        });

        console.log('API response:', response.data); // Log the entire response data

        // Check the response structure
        const data = response.data;
        console.log(data.data);
        if (data.success) {
          console.log('Setting results with data:', data.data); // Debug log for data
          setResults(data.data); // Ensure only the array of results is stored
          setQueryTitle(automaticQueries[queryId]);
        } else {
          console.log('No data found, setting empty results');
          setResults([]);
          setQueryTitle('No Results Found');
        }
      } catch (err) {
        setError('An error occurred while fetching results.');
        console.error('Error during fetch:', err);
      }
    };

    fetchQueryResults();
  }, [queryId]);

  const handleBackClick = () => {
    navigate('/restaurants');
  };

  const renderCardHeader = (item) => {
    console.log('Rendering card header for item:', item); // Debug log for item
    return item.restaurantName || item.menuName || item.RestaurantName || item.MenuName || item.Name || item.name ||automaticQueries[queryId];
  };
  const renderCardBody = (item) => {
    const details = [];
    for (const key in item) {
      if (item.hasOwnProperty(key) && key !== 'id') {
        details.push(
          <div className="item-row" key={key}>
            <span className="item-attributes-name">{key.replace(/([A-Z])/g, ' $1').trim()}:</span>
            <span>{item[key] || 'N/A'}</span>
          </div>
        );
      }
    }
    console.log('Rendering card body details:', details); // Debug log for details
    return details;
  };

  return (
    <Container fluid className="query-results-container">
      <h3 className="message-title">Showing Results of Query: {queryTitle}</h3>
      <div className="message-button">
        <Button onClick={handleBackClick} className="btn-show-default btn-back-to-restaurants">
          Back to Restaurants
        </Button>
      </div>

      {error && (
        <div className="error-overlay">
          <p className="error-text">Error: {error}</p>
        </div>
      )}

      {!error && results.length === 0 && (
        <div className="no-results-overlay">
          <p className="no-results-text">Waiting to Fetch Data.</p>
        </div>
      )}

      {results.length > 0 && (
        <Row className="g-4">
          {results.map((item, index) => (
            <Col
              key={index}
              className={`mb-4 ${results.length <= 1 ? 'col-12' : results.length === 2 ? 'col-6' : 'col-12 col-md-6 col-lg-4'}`}
            >
              <Card className="query-results-card-custom mb-4">
                <Card.Header className="query-results-card-header">
                  {renderCardHeader(item)}
                </Card.Header>
                <Card.Body className="card-body">
                  {/* <Card.Title className="query-id">{`ID: ${item.id || index}`}</Card.Title> */}
                  <div className="query-details">
                    {renderCardBody(item)}
                  </div>
                </Card.Body>
              </Card>
            </Col>
          ))}
        </Row>
      )}
    </Container>
  );
};

export default QueryResults;
