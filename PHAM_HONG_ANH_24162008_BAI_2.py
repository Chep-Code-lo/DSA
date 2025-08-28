import json
import os
# Các lựa chọn cho bánh mì
size_options = ["500ml", "250ml"]
coffe_options = ["espresso", "latte", "cappuccino"]
milk_options = ["fresh", "almond", "oat"]
topping_options = ["Whipped cream", "Caramel", "Chocolate", "Cinnamon", "Ice blended"]

# Khởi tạo dữ liệu bán hàng
sales_data = {
    "size": {"500ml": 0, "250ml": 0},
    "coffe": {"espresso": 0, "latte": 0, "cappuccino": 0},
    "milk": {"fresh": 0, "almond": 0, "oat": 0,}
}

# Đường dẫn tệp lưu trữ dữ liệu bán hàng
sales_data_file = 'sales_data.txt'


def load_sales_data():
    """Hàm tải dữ liệu bán hàng từ tệp văn bản."""
    if os.path.exists(sales_data_file):
        with open(sales_data_file, 'r') as file:
            return json.load(file)
    return sales_data


def save_sales_data(data):
    """Hàm lưu dữ liệu bán hàng vào tệp văn bản."""
    with open(sales_data_file, 'w') as file:
        json.dump(data, file)
def enter_number(prompt, min_val, max_val):
    while True:
        try:
            value = int(input(prompt))
            if value < min_val or value > max_val:
                print(f"Vui lòng nhập trong khoảng {min_val} đến {max_val}!")
            else:
                return value
        except ValueError:
            print("Vui lòng nhập số hợp lệ!")

def get_customer_choice():
    """Hàm nhận đầu vào từ khách hàng và xác nhận đơn hàng."""
    print("Chọn kích thước cốc:")
    for i, size in enumerate(size_options):
        print(f"{i + 1}. {size}")
    size_choice = enter_number("Nhập lựa chọn của bạn 1-2: ", 1, len(size_options)) - 1

    print("Chọn loại coffe:")
    for i, coffe in enumerate(coffe_options):
        print(f"{i + 1}. {coffe}")
    coffe_choice = enter_number("Nhập lựa chọn của bạn 1-3: ", 1, len(coffe_options)) - 1

    print("Chọn loại sữa:")
    for i, milk in enumerate(milk_options):
        print(f"{i + 1}. {milk}")
    milk_choice = enter_number("Nhập lựa chọn của bạn 1-3: ", 1, len(milk_options)) - 1

    print("Chọn tối đa 3 loại topping:")
    for i, topping in enumerate(topping_options):
        print(f"{i + 1}. {topping}")
    while True:
        try:
            topping_choice = input("Nhập lựa chọn của bạn, ngăn cách bởi dấu phẩy (VD: 1,3,5): ").split(",")
            temp = [int(i.strip()) - 1 for i in topping_choice]
            if len(temp) > 3:
                print("Bạn chỉ được chọn tối đa 3 loại topping, vui lòng chọn lại!")
                continue
            if any(i < 0 or i >= len(topping_options) for i in temp):
                print(f"Vui lòng chỉ nhập trong khoảng 1 - {len(topping_options)}!")
                continue
            temp = list(dict.fromkeys(temp))
            topping_choice = [topping_options[int(i) - 1] for i in topping_choice]
            break
        except ValueError:
            print("Vui lòng nhập số hợp lệ!!!")

    return {
        "size": size_options[size_choice],
        "coffe": coffe_options[coffe_choice],
        "milk": milk_options[milk_choice],
        "topping": topping_choice
    }


def record_choices(order, sales_data):
    """Hàm ghi lại lựa chọn của khách hàng vào dữ liệu bán hàng."""
    sales_data["size"][order["size"]] += 1
    sales_data["coffe"][order["coffe"]] += 1
    sales_data["milk"][order["milk"]] += 1


def find_most_least_popular(sales_data):
    """Hàm tìm món phổ biến nhất và ít phổ biến nhất."""
    total_coffe = sum(sales_data["coffe"].values())
    print("Tổng số ly coffe đã bán:", total_coffe)

    if total_coffe == 0:
        print("Không có dữ liệu bán hàng.")
        return

    coffe_percentage = {k: (v / total_coffe) * 100 for k, v in sales_data["coffe"].items()}
    most_popular = max(coffe_percentage, key=coffe_percentage.get)
    least_popular = min(coffe_percentage, key=coffe_percentage.get)

    print(f"Coffe phổ biến nhất: {most_popular} ({coffe_percentage[most_popular]:.2f}%)")
    print(f"Coffe ít phổ biến nhất: {least_popular} ({coffe_percentage[least_popular]:.2f}%)")
def main():
    # Tải dữ liệu bán hàng từ tệp
    sales_data = load_sales_data()
    order_number = 1
    while True:
        customer_order = get_customer_choice()
        print(f"Đơn hàng của bạn: {customer_order}")
        confirm = input("Xác nhận đơn hàng? (y/n): ")
        if confirm.lower() == 'y':
            print(f"Đơn hàng đã được xác nhận. Mã đơn hàng: {order_number}")
            order_number += 1
            record_choices(customer_order, sales_data)
            save_sales_data(sales_data)  # Lưu dữ liệu bán hàng vào tệp sau mỗi đơn hàng
        else:
            print("Đơn hàng đã bị hủy.")
        continue_order = input("Tiếp tục đặt hàng? (y/n): ")
        if continue_order.lower() != 'y':
            break
    print("Số liệu bán hàng cuối ngày:", sales_data)
    find_most_least_popular(sales_data)

if __name__ == "__main__":
    main()
