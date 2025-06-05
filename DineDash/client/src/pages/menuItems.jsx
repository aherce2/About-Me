import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Button from "react-bootstrap/Button";
import Col from 'react-bootstrap/Col';
import Row from 'react-bootstrap/Row';
import Container from 'react-bootstrap/Container';
import Card from 'react-bootstrap/Card';
import "./menuItems.css";
import axios from 'axios';

const MenuItems = ({ setActivePage }) => {
    const navigate = useNavigate();
    const { restaurantId } = useParams(); // Get restaurantId from URL params

    const [menuItems, setMenuItems] = useState([]);
    const [restaurant, setRestaurant] = useState(null);
    const [selectedMenuItems, setSelectedMenuItems] = useState({});
    const [buttonText, setButtonText] = useState("Order Here");
    const [cardsClickable, setCardsClickable] = useState(true);
    const [orderPlaced, setOrderPlaced] = useState(false);

    useEffect(() => {
        const storedMenu = JSON.parse(localStorage.getItem(`menu_${restaurantId}`));

        if (storedMenu) {
            if (storedMenu.restaurantId) {
                setRestaurant(storedMenu.restaurantName);
            }
            setMenuItems(storedMenu.menuItems);
        } else {
            console.error('No menu data found in local storage.');
        }
    }, [restaurantId]);

    const handleBackToRestaurant = () => {
        setActivePage(true);
        navigate(-1);
    };

    const handleOrder = () => {
        if (buttonText === "Order Here") {
            setCardsClickable(true);
            setButtonText("Place Order");
            setOrderPlaced(false);
        } else {
            if (Object.keys(selectedMenuItems).length > 0) {

                const orderDetails = {
                    Restaurant: restaurant,
                    RestaurantID: restaurantId, // Include restaurantId in the output
                    Items: Object.keys(selectedMenuItems).map(menuId => ({
                        MenuID: parseInt(menuId),
                        MenuItem: menuItems.find(item => item.MenuID === parseInt(menuId)).MenuName,
                        Price: menuItems.find(item => item.MenuID === parseInt(menuId)).Price,
                        Quantity: selectedMenuItems[menuId].quantity
                    })),
                    
                };

                console.log("Order Details:", orderDetails);
                setSelectedMenuItems({});
                setButtonText("Order Here");
                setCardsClickable(false);
                setOrderPlaced(true);
                PlaceOrder(orderDetails);
            } else {
                console.log("No items selected.");
            }
        }
    };

    const handleCardClick = (menuId) => {
        if (cardsClickable) {
            setSelectedMenuItems(prevState => {
                const newState = { ...prevState };
                if (newState[menuId]) {
                    delete newState[menuId];
                } else {
                    const item = menuItems.find(item => item.MenuID === menuId);
                    newState[menuId] = { 
                        quantity: 1, 
                        name: item.MenuName,
                        price: item.Price
                    };
                }
                return newState;
            });
        }
    };

    const handleQuantityChange = (menuId, quantity) => {
        setSelectedMenuItems(prevState => ({
            ...prevState,
            [menuId]: { 
                ...prevState[menuId], 
                quantity: parseInt(quantity, 10) 
            }
        }));
    };

    const handleInputClick = (e) => {
        e.stopPropagation(); // Prevents the event from bubbling up to the card
    };

    useEffect(() => {
        if (Object.keys(selectedMenuItems).length > 0) {
            setButtonText("Place Order");
        } else {
            setButtonText("Order Here");
        }
    }, [selectedMenuItems]);

    const PlaceOrder = async (orderDetails) => {
        try {
            const user_id = localStorage.getItem('userId');
            const response = await axios.post('http://localhost:5000/api/createorder', {user_id, orderDetails});
            console.log(response.data);

        } catch (error) {
            console.error(error);
        }
    };

    return (
    <Container fluid>
        <div className="menu-items-container">
        {restaurant ? (<><h2 className="menuItem-restaurant-title">{restaurant}</h2>
            <div className="menuItem-button-group"><Button className="me-2 btn-back" onClick={handleBackToRestaurant}>Back to Restaurant Page</Button><Button className="btn-order" onClick={handleOrder} disabled={buttonText === "Order Here" && !cardsClickable}>{buttonText}</Button></div>
            {orderPlaced && <p className="order-confirmation">Order has been placed. View in Orders page.</p>}<Row className="g-4">
                {menuItems.length > 0 ? (menuItems.map(item => (
                    <Col key={item.MenuID} className={`mb-4 ${menuItems.length === 1 ? 'col-12' : menuItems.length === 2 ? 'col-6' : 'col-12 col-md-6 col-lg-4'}`}>
                    <Card className={`menu-items-card-custom mb-4 ${selectedMenuItems[item.MenuID] ? 'highlighted' : ''} ${buttonText === "Order Here" ? 'non-clickable' : 'clickable'}`}onClick={() => handleCardClick(item.MenuID)}>
                        <Card.Header className="menu-item-page-card-header"><div className="menu-item-page-info menu-item-page-name">{item.MenuName}</div></Card.Header>
                        <Card.Body className="card-body">
                        <Card.Title className="menu-item-page-id">{`Menu ID: ${item.MenuID}`}</Card.Title>
                        <div className="menu-item-page-details">
                            <div className="item-row">
                            <span className="menu-item-page-attributes-name">Category:</span><span className="menu-item-page-details">{item.Category}</span></div>
                            <div className="item-row">
                            <span className="menu-item-page-attributes-name">Description:</span> <div className="description-box"><span>{item.Description}</span></div> </div>
                            <div className="item-row"><span className="menu-item-page-attributes-name">Price:</span><span className="menu-item-page-details">{item.Price}</span></div>
                            <div className="item-row"><span className="menu-item-page-attributes-name">Vegetarian:</span><span className="menu-item-page-details">{item.Vegetarian ? 'Yes' : 'No'}</span></div>
                            <div className="item-row"><span className="menu-item-page-attributes-name">Halal:</span><span className="menu-item-page-details">{item.Halal ? 'Yes' : 'No'}</span></div>
                            {selectedMenuItems[item.MenuID] && (
                            <div className="item-row quantity-row">
                                <span className="menu-item-page-attributes-name">Quantity:</span>
                                <input 
                                type="number" 
                                value={selectedMenuItems[item.MenuID].quantity} 
                                min="1" 
                                onChange={(e) => handleQuantityChange(item.MenuID, e.target.value)} 
                                onClick={handleInputClick} // Prevent card deselection
                                className="quantity-input"
                                />
                            </div>
                            )}</div>
                        </Card.Body>
                    </Card>
                    </Col>
                ))
                ) : (
                <p>No menu items available for this restaurant.</p>
                )}
            </Row>
            </>
        ) : (
            <p>Loading restaurant information...</p>
        )}
        </div>
    </Container>
    );
};

export default MenuItems;