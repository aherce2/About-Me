export const queryFilters  = {
  restaurantFilters: {
    PriceRange: {
      label: "Price Range",
      type: "checkbox",
      options: ["$", "$$", "$$$", "$$$$"],
    },
    ZipCode: {
      label: "Zipcode",
      type: "checkbox",
      options: ["Same zipcode", "Any zipcode"],
    },
    Score: {
      label: "Score",
      type: 'range',
      options: {
        min: 0,
        max: 5,
        step: 0.1
      }
    },
    OverallRating: {
      label: "Overall Rating",
      type: 'range',
      options: {
        min: 0,
        max: 5,
        step: 0.1
      }
    },
    RatingCount: {
      label: "Rating Count",
      type: 'range',
      options: {
        min: 0,
        max: 500,
        step: 10
      }
    }
  },
  menuItemsFilters: {
    DietaryRestrictions: {
      label: "Dietary Restrictions",
      type: "checkbox",
      options: ["Vegetarian", "Halal"],
    },
    Category: {
      label: "Category",
      type: "text",
      placeholder: "Enter category",
    },
    Name: {
      label: "Name",
      type: 'text',
      placeholder: 'Enter Name'
    },
    Price: {
      label: 'Price',
      type: 'range',
      options: {
        min: 0,
        max: 100
      }
    }
  },
};
