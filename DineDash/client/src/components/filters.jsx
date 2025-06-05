import React, { useState, useEffect } from "react";
import { Col, Container, Dropdown, ListGroup, Row } from "react-bootstrap";
import { queryFilters } from "./filtersConfig";
import "./filters.css";

export default function Filters({activePage, setRestaurantFiltersApplied, onFiltersChange }) {

  const [selectedFilters, setSelectedFilters] = useState(
    Object.keys(queryFilters).reduce((appliedFilters, filterDict) => {
      appliedFilters[filterDict] = {};
      Object.keys(queryFilters[filterDict]).forEach((filterType) => {
        const filterConfig = queryFilters[filterDict][filterType];

        if (filterConfig.type === "checkbox") {
          appliedFilters[filterDict][filterType] = {};
          filterConfig.options.forEach((option) => {
            appliedFilters[filterDict][filterType][option] = false;
          });
        } else if (filterConfig.type === "text") {
          const storedValue = localStorage.getItem(`${filterDict}-${filterType}`) || "";
          appliedFilters[filterDict][filterType] = storedValue;
        } else if (filterConfig.type === "range") {
          appliedFilters[filterDict][filterType] = {
            min: "", // Set min to an empty string
            max: "", // Set max to an empty string
          };
        }
      });
      return appliedFilters;
    }, {})
  );

  const [showDropdown, setShowDropdown] = useState(false);

  useEffect(() => {
    const savedFilters = { ...selectedFilters };
    Object.keys(queryFilters).forEach((filterDict) => {
      Object.keys(queryFilters[filterDict]).forEach((filterType) => {
        const filterConfig = queryFilters[filterDict][filterType];

        if (filterConfig.type === "text") {
          const storedValue = localStorage.getItem(`${filterDict}-${filterType}`) || "";
          savedFilters[filterDict][filterType] = storedValue;
        }
      });
    });
    setSelectedFilters(savedFilters);
  }, []);

  const handleCheckboxChange = (filterDict, filterType, option) => {
    setSelectedFilters((prevSelectedFilters) => ({
      ...prevSelectedFilters,
      [filterDict]: {
        ...prevSelectedFilters[filterDict],
        [filterType]: {
          ...prevSelectedFilters[filterDict][filterType],
          [option]: !prevSelectedFilters[filterDict][filterType][option],
        },
      },
    }));
  };

  const handleTextInputChange = (filterDict, filterType, value) => {
    const singleWord = value.replace(/\s+/g, "");

    setSelectedFilters((prevSelectedFilters) => ({
      ...prevSelectedFilters,
      [filterDict]: {
        ...prevSelectedFilters[filterDict],
        [filterType]: singleWord,
      },
    }));

    localStorage.setItem(`${filterDict}-${filterType}`, singleWord);
  };

  const handleRangeInputChange = (filterDict, filterType, minOrMax, value) => {
    const { min, max } = queryFilters[filterDict][filterType].options;
    if (value >= min && value <= max) {
      setSelectedFilters((prevSelectedFilters) => ({
        ...prevSelectedFilters,
        [filterDict]: {
          ...prevSelectedFilters[filterDict],
          [filterType]: {
            ...prevSelectedFilters[filterDict][filterType],
            [minOrMax]: value,
          },
        },
      }));
    }
  };

  const handleSubmit = (e) => {
    e.stopPropagation();
    const appliedFilters = Object.keys(selectedFilters).reduce((filters, filterDict) => {
      filters[filterDict] = {};

      Object.keys(selectedFilters[filterDict]).forEach((filterType) => {
        const filterInfo = queryFilters[filterDict][filterType];

        if (filterInfo.type === "checkbox") {
          const checkedOptions = Object.entries(selectedFilters[filterDict][filterType])
            .filter(([option, isChecked]) => isChecked)
            .map(([option]) => option);

          if (checkedOptions.length > 0) {
            filters[filterDict][filterType] = checkedOptions;
          }
        } else if (filterInfo.type === "text") {
          const textValue = selectedFilters[filterDict][filterType];
          if (textValue.trim() !== "") {
            filters[filterDict][filterType] = textValue;
          }
        } else if (filterInfo.type === "range") {
          const rangeValue = selectedFilters[filterDict][filterType];
          if (
            rangeValue.min !== filterInfo.options.min ||
            rangeValue.max !== filterInfo.options.max
          ) {
            filters[filterDict][filterType] = rangeValue;
          }
        }
      });

      return filters;
    }, {});

    console.log("Selected Filters:", appliedFilters);
    onFiltersChange(appliedFilters);
    setShowDropdown(false); // Close the dropdown when filters are applied
  };

  const handleDeselectAll = (e) => {
    e.stopPropagation();
    localStorage.removeItem('filteredResults');
    setRestaurantFiltersApplied(false); 
    const clearedFilters = Object.keys(queryFilters).reduce((filters, filterDict) => {
      filters[filterDict] = {};

      Object.keys(queryFilters[filterDict]).forEach((filterType) => {
        const filterConfig = queryFilters[filterDict][filterType];

        if (filterConfig.type === "checkbox") {
          filters[filterDict][filterType] = {};
          filterConfig.options.forEach((option) => {
            filters[filterDict][filterType][option] = false;
          });
        } else if (filterConfig.type === "text") {
          filters[filterDict][filterType] = "";
          localStorage.removeItem(`${filterDict}-${filterType}`);
        } else if (filterConfig.type === "range") {
          filters[filterDict][filterType] = {
            min: filterConfig.options.min,
            max: filterConfig.options.max,
          };
        }
      });

      return filters;
    }, {});

    setSelectedFilters(clearedFilters);
    console.log("Selected Filters Cleared:", {});

    onFiltersChange({});
  };

  const filtersToDisplay = activePage ? "restaurantFilters" : "menuItemsFilters";
  return (
    <div>
      <Dropdown className="filter-dropdown" show={showDropdown} onToggle={() => setShowDropdown(prev => !prev)}>
        <Dropdown.Toggle onClick={() => setShowDropdown(prev => !prev)}>Filter</Dropdown.Toggle>
        <Dropdown.Menu>
          <Container className="filter-container">
            <Row style={{ minWidth: "50vw" }}>
              {Object.keys(queryFilters[filtersToDisplay]).map((filterType, index) => {
                const filterConfig = queryFilters[filtersToDisplay][filterType];
                return (
                  <Col xs={12} className="filter-section" key={index}>
                    <h5 className="filter-label">{filterConfig.label}</h5>
                    <ListGroup className="filter-options">
                      <Row className="flex-nowrap">
                        {filterConfig.type === "checkbox" &&
                          filterConfig.options.map((option, id) => (
                            <Col key={id} xs="auto" className="align-items-center">
                              <div className="checkbox-item">
                                <input
                                  type="checkbox"
                                  checked={selectedFilters[filtersToDisplay][filterType][option]}
                                  onChange={() => handleCheckboxChange(filtersToDisplay, filterType, option)}
                                  className="styled-checkbox"
                                  disabled={filtersToDisplay === "menuItemsFilters"}
                                />
                                <label className="option-label">{option}</label>
                              </div>
                            </Col>
                          ))}
                        {filterConfig.type === "text" && (
                          <Col xs={12} className="text-input-container">
                            <input
                              type="text"
                              value={selectedFilters[filtersToDisplay][filterType]}
                              onChange={(e) => handleTextInputChange(filtersToDisplay, filterType, e.target.value)}
                              className="text-input"
                              disabled={filtersToDisplay === "menuItemsFilters"}
                            />
                          </Col>
                        )}
                        {filterConfig.type === "range" && (
                          <Col xs={12} className="range-input-container">
                            <label className="range-input-label">
                              <span className="range-label-text">Min:</span>
                              <input
                                type="number"
                                value={selectedFilters[filtersToDisplay][filterType].min}
                                onChange={(e) => handleRangeInputChange(filtersToDisplay, filterType, "min", e.target.value)}
                                placeholder={filterConfig.options.min}
                                className="range-input"
                                disabled={filtersToDisplay === "menuItemsFilters"}
                              />
                            </label>
                            <label className="range-input-label">
                              <span className="range-label-text">Max:</span>
                              <input
                                type="number"
                                value={selectedFilters[filtersToDisplay][filterType].max}
                                onChange={(e) => handleRangeInputChange(filtersToDisplay, filterType, "max", e.target.value)}
                                placeholder={filterConfig.options.max}
                                className="range-input"
                                disabled={filtersToDisplay === "menuItemsFilters"}
                              />
                            </label>
                          </Col>
                        )}
                      </Row>
                    </ListGroup>
                  </Col>
                );
              })}
            </Row>
            {filtersToDisplay === "menuItemsFilters" && (
              <div className="filter-overlay">
                <span className="filter-overlay-text">Menu Item Filtering For Paid Members Only</span>
              </div>
            )}
            <br />
            <Dropdown.Divider />
            <Dropdown.Item className="dropdown-item">
              <div className="dropdown-actions">
                <button className="filters-btn" onClick={handleSubmit} disabled={filtersToDisplay === "menuItemsFilters"}>Apply</button>
                <button className="filters-btn" onClick={handleDeselectAll} disabled={filtersToDisplay === "menuItemsFilters"}>Deselect All</button>
              </div>
            </Dropdown.Item>
          </Container>
        </Dropdown.Menu>
      </Dropdown>
    </div>
  );
}