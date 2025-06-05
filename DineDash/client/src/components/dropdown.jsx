import React from 'react';
import Dropdown from 'react-bootstrap/Dropdown';
import './dropdown.css';

const DropdownMenu = ({ options, onSelect }) => {
  return (
    <Dropdown className="dropdown-container">
      <Dropdown.Toggle variant="success" id="dropdown-basic">
        Select Query
      </Dropdown.Toggle>
      <Dropdown.Menu className="dropdown-menu">
        {options.map((option, index) => (
          <Dropdown.Item
            key={index}
            onClick={() => onSelect(option, index)}
            className="dropdown-item"
          >
            {option}
          </Dropdown.Item>
        ))}
      </Dropdown.Menu>
    </Dropdown>
  );
};

export default DropdownMenu;
