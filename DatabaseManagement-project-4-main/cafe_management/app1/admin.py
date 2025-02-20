from django.contrib import admin
from .models import Clerk, MenuItem, Cart, Contains, Order, Payment

# Register your models with the admin site
admin.site.register(Clerk)
admin.site.register(MenuItem)
admin.site.register(Cart)
admin.site.register(Contains)
admin.site.register(Order)
# admin.site.register(OrderedItem)
admin.site.register(Payment)
