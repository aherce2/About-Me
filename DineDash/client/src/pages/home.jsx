
import React from "react";
import Restaurants from "./restaurants";

const Home = ({RestaurantFiltersApplied, setRestaurantFiltersApplied, setActivePage}) => {
    return (
    
        <div>
        <Restaurants RestaurantFiltersApplied={RestaurantFiltersApplied} setRestaurantFiltersApplied={setRestaurantFiltersApplied} setActivePage={setActivePage} />
        </div>     
        
        
      );
}
export default Home