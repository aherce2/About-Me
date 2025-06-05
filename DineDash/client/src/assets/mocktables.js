
// Mock restaurant data
export const  mockRestaurants = [
  {
    RestaurantID: 1,
    Name: "Casey's (1306 4 Mile Rd)",
    Category: "Pizza",
    Zipcode: 75062,
    PriceRange: "$$",
  },
  {
    RestaurantID: 2,
    Name: "The Burger House",
    Category: "American",
    Zipcode: 75062,
    PriceRange: "$",
  },
  {
    RestaurantID: 3,
    Name: "Pasta Palace",
    Category: "Italian",
    Zipcode: 75062,
    PriceRange: "$$$",
  },
  {
    RestaurantID: 4,
    Name: "Sushi World",
    Category: "Japanese",
    Zipcode: 75062,
    PriceRange: "$$$",
  },
  {
    RestaurantID: 5,
    Name: "Taco Town",
    Category: "Mexican",
    Zipcode: 75062,
    PriceRange: "$",
  },
];

// Mock ratings data
export const  mockRatings = [
  { RestaurantID: 1, Score: 4.5, Ratings: 150 },
  { RestaurantID: 2, Score: 4.0, Ratings: 100 },
  { RestaurantID: 3, Score: 3.5, Ratings: 50 },
  { RestaurantID: 4, Score: 4.7, Ratings: 200 },
  { RestaurantID: 5, Score: 4.3, Ratings: 90 },
];

// Mock menu items data
export const mockMenuItems = [
  // Items for Casey's
  {
    MenuID: 1,
    Name: "Margherita Pizza",
    RestaurantID: 1,
    Category: "Pizza",
    Description: "Classic pizza with fresh tomatoes, mozzarella, and basil.",
    Price: 12.99,
    Vegetarian: true,
    Halal: true,
  },
  {
    MenuID: 2,
    Name: "Pepperoni Pizza",
    RestaurantID: 1,
    Category: "Pizza",
    Description: "Pizza topped with pepperoni slices and mozzarella.",
    Price: 14.99,
    Vegetarian: false,
    Halal: false,
  },
  
  // Items for The Burger House
  {
    MenuID: 3,
    Name: "Cheeseburger",
    RestaurantID: 2,
    Category: "Burger",
    Description: "Juicy beef patty with melted cheese, lettuce, and tomato.",
    Price: 10.99,
    Vegetarian: false,
    Halal: false,
  },
  {
    MenuID: 4,
    Name: "Veggie Burger",
    RestaurantID: 2,
    Category: "Burger",
    Description: "A delicious plant-based patty with all the fixings.",
    Price: 11.99,
    Vegetarian: true,
    Halal: true,
  },

  // Items for Pasta Palace
  {
    MenuID: 5,
    Name: "Spaghetti Carbonara",
    RestaurantID: 3,
    Category: "Pasta",
    Description: "Pasta with a creamy sauce made from eggs, cheese, pancetta, and pepper.",
    Price: 13.99,
    Vegetarian: false,
    Halal: false,
  },
  {
    MenuID: 6,
    Name: "Vegetarian Lasagna",
    RestaurantID: 3,
    Category: "Pasta",
    Description: "Lasagna with layers of pasta, cheese, and mixed vegetables.",
    Price: 14.99,
    Vegetarian: true,
    Halal: true,
  },

  // Items for Sushi World
  {
    MenuID: 7,
    Name: "California Roll",
    RestaurantID: 4,
    Category: "Sushi",
    Description: "Sushi roll with crab meat, avocado, and cucumber.",
    Price: 9.99,
    Vegetarian: false,
    Halal: false,
  },
  {
    MenuID: 8,
    Name: "Vegetable Tempura Roll",
    RestaurantID: 4,
    Category: "Sushi",
    Description: "Roll filled with tempura vegetables and served with soy sauce.",
    Price: 10.99,
    Vegetarian: true,
    Halal: true,
  },

  // Items for Taco Town
  {
    MenuID: 9,
    Name: "Beef Tacos",
    RestaurantID: 5,
    Category: "Tacos",
    Description: "Soft tacos filled with seasoned beef, lettuce, and cheese.",
    Price: 8.99,
    Vegetarian: false,
    Halal: false,
  },
  {
    MenuID: 10,
    Name: "Vegetarian Tacos",
    RestaurantID: 5,
    Category: "Tacos",
    Description: "Tacos filled with spiced beans, lettuce, and cheese.",
    Price: 9.99,
    Vegetarian: true,
    Halal: true,
  },
];

export default mockMenuItems;
