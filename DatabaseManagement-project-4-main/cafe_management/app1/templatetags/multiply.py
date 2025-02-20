# In your Django app's templatetags/multiply.py file

from django import template

register = template.Library()

@register.filter
def multiply(value, arg):
    return value * arg
