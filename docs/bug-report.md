# Bug Reports

## BUG-001: Product stock was not restored after order cancellation

**Severity:** High  
**Priority:** High  
**Status:** Fixed

### Steps to reproduce

1. Create a product with stock quantity 10.
2. Place an order for quantity 2.
3. Confirm that stock becomes 8.
4. Cancel the order.
5. View the product again.

### Expected result

The product stock should return to 10.

### Actual result

The product stock remained 8.

### Root cause

The order status was changed to CANCELLED, but the ordered quantity was not added back to the product stock.

### Fix

Added stock-restoration logic inside the `cancelOrder()` method.

### Retest result

Passed.

### Regression result

Order placement, stock reduction and cancellation tests passed.