import React, { useEffect, useState } from 'react';
import axios from 'axios';
import Card from 'react-bootstrap/Card';
import './orders.css';
import Alert from 'react-bootstrap/Alert';

const Orders = () => {
  const [orders, setOrders] = useState([]);
  const [editingOrderId, setEditingOrderId] = useState(null);
  const [orderChanges, setOrderChanges] = useState({});
  const [deletedItems, setDeletedItems] = useState({});
  const [alertMessage, setAlertMessage] = useState('');
  const [orderPlaced, setOrderPlaced] = useState(null);
  const userId = localStorage.getItem("userId");

  useEffect(() => {
    const fetchOrders = async () => {
      const url = `http://localhost:5000/api/users/${userId}/orders`;
      try {
        const response = await axios.get(url);
        setOrders(response.data);

        // Check which orders have been placed
        const placedOrders = response.data.map(order => {
          if (localStorage.getItem(`orderPlaced_${order.OrderID}`)) {
            return order.OrderID;
          }
          return null;
        }).filter(id => id !== null);
        setOrderPlaced(placedOrders[0] || null); // Set the first placed order ID

        console.log('Orders data: ', response.data);
      } catch (error) {
        console.log('Error fetching orders: ', error);
      }
    };

    fetchOrders();
  }, [userId]);
  const isOrderPlaced = (orderId) => {
    return !!localStorage.getItem(`orderPlaced_${orderId}`);
  };

  const placeOrder = (orderId) => {
    localStorage.setItem(`orderPlaced_${orderId}`, true); // Save to localStorage
    setAlertMessage("Your order has successfully been placed!");
    setOrderPlaced(orderId); // Set the order placed state to the current order ID
  };

  const handleDeleteUndo = (orderId, menuItemId) => {
    setOrderChanges((prev) => ({
      ...prev,
      [orderId]: {
        ...prev[orderId],
        deletedItems: {
          ...prev[orderId]?.deletedItems,
          [menuItemId]: undefined,
        },
        modifiedItems: {
          ...prev[orderId]?.modifiedItems,
          [menuItemId]: undefined,
        }
      }
    }));

    setDeletedItems((prev) => {
      const updatedDeletedItems = { ...prev };
      if (updatedDeletedItems[orderId]) {
        const updatedItems = { ...updatedDeletedItems[orderId] };
        delete updatedItems[menuItemId];
        updatedDeletedItems[orderId] = Object.keys(updatedItems).length ? updatedItems : undefined;
      }
      return updatedDeletedItems;
    });
  };
  const deleteOrderItem = (orderId, menuItemId) => {
    setOrderChanges((prev) => ({
      ...prev,
      [orderId]: {
        ...prev[orderId],
        deletedItems: {
          ...prev[orderId]?.deletedItems,
          [menuItemId]: true,
        },
        modifiedItems: {
          ...prev[orderId]?.modifiedItems,
          [menuItemId]: undefined,
        }
      }
    }));
    setDeletedItems((prev) => ({
      ...prev,
      [orderId]: {
        ...prev[orderId],
        [menuItemId]: true,
      }
    }));
  };

  const modifyOrder = (orderId) => {
    setEditingOrderId(orderId);
  };

  const discardChanges = (orderId) => {
    setOrderChanges((prev) => {
      const updatedChanges = { ...prev };
      delete updatedChanges[orderId];
      return updatedChanges;
    });
    setDeletedItems((prev) => {
      const updatedDeletedItems = { ...prev };
      delete updatedDeletedItems[orderId];
      return updatedDeletedItems;
    });
    setEditingOrderId(null);
  };

  const handleQuantityChange = (orderId, menuItemId, quantity) => {
    setOrderChanges((prev) => ({
      ...prev,
      [orderId]: {
        ...prev[orderId],
        modifiedItems: {
          ...prev[orderId]?.modifiedItems,
          [menuItemId]: { quantity }
        }
      }
    }));
  };


  const updateOrder = (orderId) => {
    const changes = orderChanges[orderId] || {};
    const updatedOrder = {
        updateItems: [],
        deleteItems: []
    };

    if (changes.modifiedItems) {
        Object.entries(changes.modifiedItems).forEach(([menuItemId, item]) => {
            if (item && item.quantity !== undefined) {
                updatedOrder.updateItems.push({
                  OrderID: orderId,
                  MenuID: parseInt(menuItemId, 10),
                  Quantity: parseFloat(item.quantity)
                });
            }
        });
    }

    if (changes.deletedItems) {
        Object.entries(changes.deletedItems).forEach(([menuItemId]) => {
            updatedOrder.deleteItems.push({
              OrderID: orderId,
              MenuID: parseInt(menuItemId, 10)
            });
        });
    }
    modifyOrderQuantity(orderId, updatedOrder);

    console.log("Updated Order Data:", updatedOrder);

    // Reset changes and editing state
    setOrderChanges((prev) => {
        const updatedChanges = { ...prev };
        delete updatedChanges[orderId];
        return updatedChanges;
    });
    setDeletedItems((prev) => {
        const updatedDeletedItems = { ...prev };
        delete updatedDeletedItems[orderId];
        return updatedDeletedItems;
    });

    setEditingOrderId(null);
  };
  const deleteOrder = async (userId, orderId, e) => {
    e.preventDefault();
    console.log('Deleting Order Data');
    try {
      if (!orderId) {
        console.error("Order ID not found");
        return;
      }

      const response = await axios.delete(`http://localhost:5000/api/users/${userId}/orders/${orderId}`);

      if (response.status === 200) {
        console.log('Order deleted successfully');
        // Update the state to remove the deleted order
        setOrders((prevOrders) => prevOrders.filter(order => order.OrderID !== orderId));
      } else {
        console.error('Failed to delete order');
      }
    } catch (error) {
      console.error('An error occurred while deleting the order:', error);
    }
  };
  
  const modifyOrderQuantity = async (orderId, updatedItems) => {
    console.log('Editing Order Data');
    try {

      const response = await axios.post(`http://localhost:5000/api/orders/updateorder`, { updatedItems });

      if (response.status === 200) {
        console.log('Order edited successfully');
        // Update the state to remove the deleted order
        setOrders((prevOrders) => prevOrders.filter(order => order.OrderID !== orderId));
      } else {
        console.error('Failed to edit order');
      }
    } catch (error) {
      console.error('An error occurred while editing the order:', error);
    }
  };

  return (
<div className="orders-container">
      {alertMessage && <div className="custom-alert">{alertMessage}</div>}
      {orders.map((order) => (<Card className={`card-custom ${isOrderPlaced(order.OrderID) ? 'order-placed' : ''}`}key={order.OrderID} >
          <Card.Header className="card-header"><div className="order-info"><span className="order-id">{`Order ID: ${order.OrderID}`}</span><span className="restaurant-name">{order.Restaurant}</span></div></Card.Header>
          <Card.Body className="card-body">
            {order.Items.map((item, index) => (
              <div key={index} className={`item-row ${deletedItems[order.OrderID]?.[item.MenuID] ? 'deleted-item' : ''}`}>
                <span className="menu-item-name">
                  {item.MenuItem}
                  {editingOrderId === order.OrderID && !isOrderPlaced(order.OrderID) && (
                    <>{deletedItems[order.OrderID]?.[item.MenuID] ? (<Card.Link className="orders-card-link" onClick={() => handleDeleteUndo(order.OrderID, item.MenuID)}>Undo Delete</Card.Link>
                      ) : (
                        <Card.Link className="orders-card-link" onClick={() => deleteOrderItem(order.OrderID, item.MenuID)}>Delete Item</Card.Link>
                      )}
                    </>
                  )}
                </span>
                <span className="quantity">{`Qty: ${item.Quantity}`}
                  {editingOrderId === order.OrderID && !deletedItems[order.OrderID]?.[item.MenuID] && !isOrderPlaced(order.OrderID) && (
                    <input 
                      type="number" 
                      value={orderChanges[order.OrderID]?.modifiedItems?.[item.MenuID]?.quantity ?? item.Quantity} 
                      onChange={(e) => handleQuantityChange(order.OrderID, item.MenuID, e.target.value)} 
                      className="quantity-input" 
                      disabled={deletedItems[order.OrderID]?.[item.MenuID]} 
                    />
                  )}
                </span>
                <span className="price">{`$${item.Price.toFixed(2)}`}</span></div>))}
            <div className="total-price-container"><span className="total-price"><strong className="total-price-label">Total Price:</strong> ${order.TotalPrice.toFixed(2)}</span>
            <div className="links-container">
                {editingOrderId === order.OrderID ? (
                  <>
                    <Card.Link className="orders-card-link" onClick={() => discardChanges(order.OrderID)}>Discard Changes</Card.Link>
                    <Card.Link className="orders-card-link" onClick={() => updateOrder(order.OrderID)}>Update Order</Card.Link>
                  </>
                ) : (
                  !isOrderPlaced(order.OrderID) && (
                    <Card.Link className="orders-card-link" onClick={() => modifyOrder(order.OrderID)}>Modify Order</Card.Link>
                  )
                )}
                <Card.Link className="orders-card-link" onClick={(e) => deleteOrder(userId, order.OrderID, e)}>Delete Order</Card.Link>
                {!isOrderPlaced(order.OrderID) && (
                  <Card.Link className="orders-card-link" onClick={() => placeOrder(order.OrderID)}>Place Order</Card.Link>
                )}
              </div>
            </div>
          </Card.Body>
        </Card>
      ))}
    </div>
  );
};

export default Orders;