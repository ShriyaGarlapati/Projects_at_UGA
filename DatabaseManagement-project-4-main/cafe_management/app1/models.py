from django.db import models
from django.contrib.auth.models import User

class Clerk(models.Model):
    user = models.OneToOneField(User, on_delete=models.CASCADE)
    email = models.EmailField()
    mobile_number = models.CharField(max_length=15)

    def __str__(self):
        return self.user.username

class MenuItem(models.Model):
    menu_item_name = models.CharField(max_length=100)
    category = models.CharField(max_length=100)
    description = models.TextField()
    price = models.DecimalField(max_digits=10, decimal_places=2)
    menu_item_image = models.URLField(blank=True)  # New field for image link
    def __str__(self):
        return self.menu_item_name

class Cart(models.Model):
    clerk = models.ForeignKey(Clerk, on_delete=models.CASCADE)
    customer_user_name = models.CharField(max_length=100, null=True)
    customer_mobile_number = models.CharField(max_length=15, null=True)
    menu_items = models.ManyToManyField(MenuItem, through='Contains')

    def __str__(self):
        return f"Cart for {self.customer_name} managed by {self.clerk.user.username}"

class Contains(models.Model):
    cart = models.ForeignKey(Cart, on_delete=models.CASCADE)
    menu_item = models.ForeignKey(MenuItem, on_delete=models.CASCADE)
    quantity = models.PositiveIntegerField()

    def __str__(self):
        return f"{self.cart.customer_name}'s cart contains {self.quantity} {self.menu_item.menu_item_name}"


class Order(models.Model):
    clerk = models.ForeignKey(Clerk, on_delete=models.CASCADE)
    ordered_date = models.DateTimeField(auto_now_add=True)
    payment = models.OneToOneField('Payment', on_delete=models.CASCADE, null=True, blank=True, related_name='order_payment')
    customer_user_name = models.CharField(max_length=100, null=True)
    customer_mobile_number = models.CharField(max_length=15, null=True)
    def __str__(self):
        return f"Order by {self.clerk.user.username} on {self.ordered_date}"

class OrderContains(models.Model):
    order = models.ForeignKey(Order, on_delete=models.CASCADE)
    menu_item = models.ForeignKey(MenuItem, on_delete=models.CASCADE)
    quantity = models.PositiveIntegerField()

    def __str__(self):
        return f"{self.order.customer_user_name}'s order contains {self.quantity} {self.menu_item.menu_item_name}"


# class OrderedItem(models.Model):
#     order = models.ForeignKey(Order, on_delete=models.CASCADE, related_name='ordered_items')
#     ordered_item_name = models.CharField(max_length=100)
#     category = models.CharField(max_length=100)
#     price = models.DecimalField(max_digits=10, decimal_places=2)
#     quantity = models.PositiveIntegerField()
#     ordered_item_image = models.URLField(blank=True)  # New field for image link

#     def __str__(self):
#         return f"{self.ordered_item_name} in order {self.order.id}"

class Payment(models.Model):
    order = models.ForeignKey(Order, on_delete=models.CASCADE, related_name='payment_order')
    total_payment = models.DecimalField(max_digits=10, decimal_places=2)
    payment_type = models.CharField(max_length=100)

    def __str__(self):
        return f"Payment for order {self.order.id}: {self.total_payment} {self.payment_type}"


