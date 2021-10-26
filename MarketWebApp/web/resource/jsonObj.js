
class Location{
    constructor() {
        this.x = NaN;
        this.y = NaN;
    }

    static calculateDistance(xLocation, yLocation, xxLocation, yyLocation){
        var powX = Math.pow((xxLocation - xLocation), 2);
        var powY = Math.pow((yyLocation - yLocation), 2);
        return (Math.sqrt(powX + powY)).toFixed(2);
    }

}

class Cart{
    constructor() {
        this.location = new Location();
        this.idToQuantity = new Map();
        this.zone = NaN;
        this.date = NaN;
        this.customer = NaN;
        this.store = NaN;
        this.dynamicOrder = true;
    }

    addItem(serial, quantity) {
        if (this.idToQuantity.get(parseInt(serial)) === undefined) {
            this.idToQuantity.set(parseInt(serial), quantity);
        } else {
            var totalQuantity = parseFloat(quantity) + parseFloat(this.idToQuantity.get(parseInt(serial)));
            this.idToQuantity.set(parseInt(serial), totalQuantity);
        }
    }
}

class Sale{
    constructor(saleName, productSerial) {
        this.saleName = saleName;
        this.productSerial = productSerial;
    }
}

class Feedback{
    constructor(storeSerial, rating, comment, date) {
        this.storeSerial = storeSerial;
        this.rating = rating;
        this.comment = comment;
        this.date = date;
    }

}

class Store{
    constructor(name, ppk, location, idToPrice, owner) {
        this.name = name;
        this.owner = owner;
        this.ppk = ppk;
        this.location = location;
        this.idToPrice = idToPrice;
    }
}

class BuyerOrderDetails{
    constructor() {
        this.productsPrice = 0;
        this.deliveryPrice = 0;
        this.numOfProducts = 0;
    }
}