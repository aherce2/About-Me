Create Procedure GetPopularRestandItems()
begin

    -- get popular restaurants
    select
    r.RestaurantID,
    r.Name,
    r.ZipCode,
    count(o.OrderID) as numorders,
    rt.Score,
    rt.Ratings,
    count(oi.MenuID)
    from restaurants r
    left join ratings rt on rt.RestaurantID = r.RestaurantID
    left join orders o on o.RestaurantID = r.RestaurantID
    left join orderitems oi on oi.OrderID = o.OrderID
    where Score > 0 and Ratings > 0
    group by r.RestaurantID, r.Name
    having numorders > 0;

    -- get popular items
    select
    oi.MenuID,
    mi.Name,
    sum(oi.quantity) as numorders,
    mi.RestaurantID,
    mi.Price,
    mi.Category,
    mi.Description,
    mi.Halal,
    mi.Vegetarian
    from orderitems oi
    left join menuitems mi on mi.MenuID = oi.MenuID
    group by oi.MenuID
    having numorders > 0;

end
