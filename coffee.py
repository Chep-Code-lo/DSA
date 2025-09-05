import json
import os

# ===== Lựa chọn cho cafe =====
SIZE_OPTIONS   = ["500ml", "250ml"]
COFFEE_OPTIONS = ["espresso", "latte", "cappuccino"]
MILK_OPTIONS   = ["fresh", "almond", "oat"]
TOPPING_OPTIONS= ["Whipped cream", "Caramel", "Chocolate", "Cinnamon", "Ice blended"]

# ===== Đường dẫn file =====
ORDERS_LOG_FILE = "sales_data.txt"        # list các đơn hàng: [{order_id, drinks, status}]
COUNTERS_FILE   = "sales_counters.json"   # bộ đếm: {"size": {...}, "coffee": {...}, "milk": {...}}

# ===== Bộ đếm mặc định =====
def _default_counters():
    return {
        "size":   {k: 0 for k in SIZE_OPTIONS},
        "coffee": {k: 0 for k in COFFEE_OPTIONS},
        "milk":   {k: 0 for k in MILK_OPTIONS},
    }

# ===== Persistence: ORDERS =====
def load_orders():
    """Đọc danh sách đơn hàng từ ORDERS_LOG_FILE (JSON list)."""
    if os.path.exists(ORDERS_LOG_FILE):
        try:
            with open(ORDERS_LOG_FILE, "r", encoding="utf-8") as f:
                data = json.load(f)
            return data if isinstance(data, list) else []
        except Exception:
            return []
    return []

def save_orders(orders):
    """Ghi danh sách đơn hàng xuống ORDERS_LOG_FILE (JSON list)."""
    with open(ORDERS_LOG_FILE, "w", encoding="utf-8") as f:
        json.dump(orders, f, ensure_ascii=False, indent=2)

# ===== Persistence: COUNTERS =====
def load_counters():
    """Đọc bộ đếm; nếu thiếu key thì bổ sung về 0."""
    base = _default_counters()
    if os.path.exists(COUNTERS_FILE):
        try:
            with open(COUNTERS_FILE, "r", encoding="utf-8") as f:
                data = json.load(f)
            # đảm bảo đầy đủ nhóm/khóa
            for g in base:
                if g not in data or not isinstance(data[g], dict):
                    data[g] = {}
                for k in base[g]:
                    if not isinstance(data[g].get(k), int):
                        data[g][k] = 0
            return data
        except Exception:
            pass
    return base

def save_counters(counters):
    """Ghi bộ đếm xuống COUNTERS_FILE."""
    with open(COUNTERS_FILE, "w", encoding="utf-8") as f:
        json.dump(counters, f, ensure_ascii=False, indent=2)

def enter_number(prompt, min_val, max_val):
    while True:
        try:
            value = int(input(prompt))
            if not (min_val <= value <= max_val):
                print(f"Vui lòng nhập trong khoảng {min_val} đến {max_val}!")
            else:
                return value
        except ValueError:
            print("Vui lòng nhập số hợp lệ!")

def get_customer_choice():
    """Nhận input 1 ly cafe & xác nhận lựa chọn."""
    print("Chọn kích thước cốc:")
    for i, size in enumerate(SIZE_OPTIONS, start=1):
        print(f"{i}. {size}")
    size_choice = enter_number("Nhập lựa chọn của bạn 1-2: ", 1, len(SIZE_OPTIONS)) - 1

    print("Chọn loại coffee:")
    for i, coffee in enumerate(COFFEE_OPTIONS, start=1):
        print(f"{i}. {coffee}")
    coffee_choice = enter_number("Nhập lựa chọn của bạn 1-3: ", 1, len(COFFEE_OPTIONS)) - 1

    print("Chọn loại sữa:")
    for i, milk in enumerate(MILK_OPTIONS, start=1):
        print(f"{i}. {milk}")
    milk_choice = enter_number("Nhập lựa chọn của bạn 1-3: ", 1, len(MILK_OPTIONS)) - 1

    print("Chọn tối đa 3 loại topping:")
    for i, topping in enumerate(TOPPING_OPTIONS, start=1):
        print(f"{i}. {topping}")

    while True:
        raw = input("Nhập lựa chọn, cách nhau bởi dấu phẩy (VD: 1,3,5) hoặc Enter để bỏ qua: ").strip()
        if raw == "":
            topping_choice = []
            break
        try:
            parts = [p.strip() for p in raw.split(",") if p.strip()]
            idxs = []
            for p in parts:
                if not p.isdigit():
                    raise ValueError
                v = int(p)
                if not (1 <= v <= len(TOPPING_OPTIONS)):
                    raise ValueError
                if v not in idxs:
                    idxs.append(v)
            if len(idxs) > 3:
                print("Bạn chỉ được chọn tối đa 3 loại topping, vui lòng chọn lại!")
                continue
            topping_choice = [TOPPING_OPTIONS[i - 1] for i in idxs]
            break
        except ValueError:
            print("Vui lòng nhập số hợp lệ trong khoảng cho phép!")

    return {
        "size":   SIZE_OPTIONS[size_choice],
        "coffee": COFFEE_OPTIONS[coffee_choice],
        "milk":   MILK_OPTIONS[milk_choice],
        "topping": topping_choice
    }

def record_drink_to_counters(drink, counters):
    """Tăng bộ đếm theo 1 ly đã bán."""
    s = drink.get("size")
    c = drink.get("coffee")
    m = drink.get("milk")
    if s in counters["size"]:
        counters["size"][s] += 1
    if c in counters["coffee"]:
        counters["coffee"][c] += 1
    if m in counters["milk"]:
        counters["milk"][m] += 1

# ===== Xây dựng đơn hàng =====
def build_order(order_id, drinks):
    """Trả về object đơn hàng theo format chuẩn."""
    return {"order_id": order_id, "drinks": drinks, "status": "confirmed"}


def print_order(order):
    print(f"Mã đơn hàng: {order['order_id']}")
    for i, d in enumerate(order["drinks"], start=1):
        tops = ", ".join(d.get("topping", [])) if d.get("topping") else "(none)"
        print(f"  Ly #{i}: size={d['size']}, coffee={d['coffee']}, milk={d['milk']}, topping=[{tops}]")
    if order.get("status") == "confirmed":
        print("✅ Đơn hàng đã được xác nhận.")

def analyze_coffee(counters):
    print("\n===== PHÂN TÍCH COFFEE =====")
    total = sum(counters["coffee"].values())
    print(f"Tổng số ly coffee đã bán: {total}")
    if total == 0:
        print("Chưa có dữ liệu.")
        return
    for k, v in counters["coffee"].items():
        pct = (v / total) * 100 if total else 0
        print(f"- {k:<11}: {v:>4} ly ({pct:6.2f}%)")

# ===== Main =====
def main():
    orders = load_orders()
    counters = load_counters()
    next_order_id = (max([o["order_id"] for o in orders], default=0) + 1)

    print("=== DỊCH VỤ ĐẶT COFFEE  ===")
    order_number = next_order_id
    while True:
        drink = get_customer_choice()
        print(f"\nĐơn hàng của bạn (1 ly): {drink}")
        confirm = input("Xác nhận đơn hàng? (y/n): ").strip().lower()
        if confirm == 'y':
            order = build_order(order_number, [drink])

            record_drink_to_counters(drink, counters)
            save_counters(counters)

            orders.append(order)
            save_orders(orders)

            print("\n===== CHI TIẾT ĐƠN HÀNG =====")
            print_order(order)
            order_number += 1
        else:
            print("❌ Đơn hàng đã bị hủy.")

        cont = input("\nTiếp tục đặt hàng? (y/n): ").strip().lower()
        if cont != 'y':
            break

    # Tổng kết đơn hàng
    print("\n===== SỐ LIỆU CUỐI NGÀY =====")
    print(json.dumps(counters, ensure_ascii=False, indent=2))
    analyze_coffee(counters)

if __name__ == "__main__":
    main()
