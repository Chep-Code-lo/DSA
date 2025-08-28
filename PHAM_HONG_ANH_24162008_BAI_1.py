import json
import os

# Các lựa chọn cho bánh mì
size_options = ["30cm", "15cm"]
bread_options = ["white", "brown", "seeded"]
filling_options = ["beef", "chicken", "cheese", "egg", "tuna", "turkey"]
salad_options = ["lettuce", "tomato", "sweetcorn", "cucumber", "peppers"]

# Khởi tạo dữ liệu bán hàng
sales_data = {
    "size": {"30cm": 0, "15cm": 0},
    "bread": {"white": 0, "brown": 0, "seeded": 0},
    "filling": {"beef": 0, "chicken": 0, "cheese": 0, "egg": 0, "tuna": 0, "turkey": 0}
}

# Đường dẫn tệp lưu trữ dữ liệu bán hàng
sales_data_file = 'sales_data_1.txt'


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
    print("Chọn kích thước bánh mì:")
    for i, size in enumerate(size_options):
        print(f"{i + 1}. {size}")
    size_choice = enter_number("Nhập lựa chọn của bạn 1-2: ", 1, len(size_options)) - 1

    print("Chọn loại bánh mì:")
    for i, bread in enumerate(bread_options):
        print(f"{i + 1}. {bread}")
    bread_choice = enter_number("Nhập lựa chọn của bạn 1-3: ", 1, len(bread_options)) - 1
    print("Chọn nhân bánh mì:")
    for i, filling in enumerate(filling_options):
        print(f"{i + 1}. {filling}")
    filling_choice = enter_number("Nhập lựa chọn của bạn 1-6: ", 1, len(filling_options)) - 1
    print("Chọn tối đa 3 loại salad:")
    for i, salad in enumerate(salad_options):
        print(f"{i + 1}. {salad}")
    while True:
        try:
            salad_choice = input("Nhập lựa chọn của bạn, ngăn cách bởi dấu phẩy (VD: 1,3,5): ").split(",")
            temp = [int(i.strip()) - 1 for i in salad_choice]
            if len(temp) > 3:
                print("Bạn chỉ được chọn tối đa 3 loại topping, vui lòng chọn lại!")
                continue
            if any(i < 0 or i >= len(salad_options) for i in temp):
                print(f"Vui lòng chỉ nhập trong khoảng 1 - {len(salad_options)}!")
                continue
            temp = list(dict.fromkeys(temp))
            salad_choice = [salad_options[int(i) - 1] for i in salad_choice]
            break
        except ValueError:
            print("Vui lòng nhập số hợp lệ!!!")
    return {
        "size": size_options[size_choice],
        "bread": bread_options[bread_choice],
        "filling": filling_options[filling_choice],
        "salad": salad_choice
    }

def record_choices(order, sales_data):
    """Hàm ghi lại lựa chọn của khách hàng vào dữ liệu bán hàng."""
    sales_data["size"][order["size"]] += 1
    sales_data["bread"][order["bread"]] += 1
    sales_data["filling"][order["filling"]] += 1

def find_most_least_popular(sales_data):
    """Hàm tìm món phổ biến nhất và ít phổ biến nhất."""
    total_baguettes = sum(sales_data["filling"].values())
    print("Tổng số bánh mì đã bán:", total_baguettes)

    if total_baguettes == 0:
        print("Không có dữ liệu bán hàng.")
        return

    filling_percentage = {k: (v / total_baguettes) * 100 for k, v in sales_data["filling"].items()}
    most_popular = max(filling_percentage, key=filling_percentage.get)
    least_popular = min(filling_percentage, key=filling_percentage.get)

    print(f"Nhân phổ biến nhất: {most_popular} ({filling_percentage[most_popular]:.2f}%)")
    print(f"Nhân ít phổ biến nhất: {least_popular} ({filling_percentage[least_popular]:.2f}%)")
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
