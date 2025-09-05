
import json, os

# ===== Options =====
# Các lựa chọn cố định cho 1 ly trà sữa
SIZE_OPTIONS    = ["500ml", "750ml"]
TEA_OPTIONS     = ["black tea", "green tea", "olong"]
MILK_OPTIONS    = ["fresh milk", "almond milk", "oat milk"]
SUGAR_OPTIONS   = ["0%", "30%", "50%", "70%", "100%"]
ICE_OPTIONS     = ["no ice", "little ice", "medium", "lots"]
TOPPING_OPTIONS = ["black pearls", "white pearls", "fruit jelly", "coffee jelly", "egg pudding", "cheese cream"]

# ===== Files =====
# File lưu đơn hàng và file thống kê
ORDERS_LOG_FILE = "sales_data.txt"
COUNTERS_FILE   = "sales_counters.json"

# ===== Default counters =====
# Khởi tạo số liệu thống kê ban đầu (tất cả = 0)
def _default_counters():
    return {
        "size":   {s: 0 for s in SIZE_OPTIONS},
        "tea":    {t: 0 for t in TEA_OPTIONS},
        "milk":   {m: 0 for m in MILK_OPTIONS},
        "sugar":  {s: 0 for s in SUGAR_OPTIONS},
        "ice":    {i: 0 for i in ICE_OPTIONS},
        "topping":{t: 0 for t in TOPPING_OPTIONS},
    }

# ===== Persistence: orders =====
# Đọc danh sách đơn hàng từ file
def load_orders():
    if os.path.exists(ORDERS_LOG_FILE):
        try:
            with open(ORDERS_LOG_FILE, "r", encoding="utf-8") as f:
                data = json.load(f)
            return data if isinstance(data, list) else []
        except Exception:
            return []
    return []

# Ghi danh sách đơn hàng ra file
def save_orders(orders):
    with open(ORDERS_LOG_FILE, "w", encoding="utf-8") as f:
        json.dump(orders, f, ensure_ascii=False, indent=2)

# ===== Persistence: counters =====
# Đọc số liệu thống kê từ file (nếu không có thì tạo mặc định)
def load_counters():
    if os.path.exists(COUNTERS_FILE):
        try:
            with open(COUNTERS_FILE, "r", encoding="utf-8") as f:
                data = json.load(f)
            base = _default_counters()
            # đảm bảo đủ key và các giá trị đều là số
            for g in base:
                if g not in data or not isinstance(data[g], dict):
                    data[g] = {}
                for k in base[g]:
                    if k not in data[g] or not isinstance(data[g][k], int):
                        data[g][k] = 0
            return data
        except Exception:
            pass
    return _default_counters()

# Ghi số liệu thống kê ra file
def save_counters(counters):
    with open(COUNTERS_FILE, "w", encoding="utf-8") as f:
        json.dump(counters, f, ensure_ascii=False, indent=2)
# Hiển thị danh sách lựa chọn (size, tea, milk, sugar, ice) cho người dùng chọn
def choose_from_list(prompt, options):
    while True:
        print(f"\n{prompt}")
        for i, opt in enumerate(options, start=1):
            print(f"{i}. {opt}")
        raw = input(f"Chọn (1-{len(options)}): ").strip()
        if raw.isdigit():
            idx = int(raw) - 1
            if 0 <= idx < len(options):
                return options[idx]
        print("⛔ Lựa chọn không hợp lệ, thử lại.")

# Chọn topping (tối đa 3 loại hoặc bỏ trống)
def choose_toppings():
    print("\nChọn tối đa 3 loại topping (nhập số, cách nhau bởi dấu phẩy).")
    for i, t in enumerate(TOPPING_OPTIONS, start=1):
        print(f"{i}. {t}")
    print("Bỏ trống hoặc nhập 0 nếu không chọn.")
    while True:
        raw = input("Ví dụ: 1,3 hoặc 2,4,5: ").strip()
        if raw == "" or raw == "0":
            return []
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
                print("⛔ Tối đa 3 topping. Hãy chọn lại.")
                continue
            return [TOPPING_OPTIONS[i-1] for i in idxs]
        except ValueError:
            print("⛔ Danh sách không hợp lệ. Hãy nhập lại.")

# ===== Build drink & order =====
# Tạo 1 ly trà sữa
def input_one_drink():
    size   = choose_from_list("Chọn size ly:", SIZE_OPTIONS)
    tea    = choose_from_list("Chọn nền trà:", TEA_OPTIONS)
    milk   = choose_from_list("Chọn loại sữa:", MILK_OPTIONS)
    sugar  = choose_from_list("Chọn mức đường:", SUGAR_OPTIONS)
    ice    = choose_from_list("Chọn mức đá:", ICE_OPTIONS)
    toppings = choose_toppings()
    drink = {"size": size, "tea": tea, "milk": milk, "sugar": sugar, "ice": ice, "topping": toppings}
    # In ra thông tin đã chọn
    print("\n✅ Ly trà sữa đã chọn:")
    for k, v in drink.items():
        print(f"   - {k}: {', '.join(v) if isinstance(v, list) and v else v if v else '(none)'}")
    return drink

# Tạo 1 đơn hàng gồm nhiều ly
def build_order(order_id, drinks):
    return {"order_id": order_id, "drinks": drinks, "status": "confirmed"}

# ===== Update counters =====
# Cập nhật số liệu thống kê theo từng ly
def record_drink_to_counters(drink, counters):
    counters["size"][drink["size"]]   += 1
    counters["tea"][drink["tea"]]     += 1
    counters["milk"][drink["milk"]]   += 1
    counters["sugar"][drink["sugar"]] += 1
    counters["ice"][drink["ice"]]     += 1
    for t in drink["topping"]:
        counters["topping"][t] += 1

# ===== Pretty printing =====
# In ra chi tiết 1 đơn hàng
def print_order(order):
    print(f"Mã đơn hàng: {order['order_id']}")
    for i, drink in enumerate(order["drinks"], start=1):
        topping_str = ", ".join(drink["topping"]) if drink["topping"] else "(none)"
        print(f"  Ly #{i}: size={drink['size']}, tea={drink['tea']}, milk={drink['milk']}, "
              f"sugar={drink['sugar']}, ice={drink['ice']}, topping=[{topping_str}]")
    if order.get("status") == "confirmed":
        print("✅ Đơn hàng đã được xác nhận.")

# Phân tích thống kê theo nền trà
def analyze_tea(counters):
    print("\n===== PHÂN TÍCH NỀN TRÀ =====")
    total = sum(counters["tea"].values())
    print(f"Tổng số trà sữa đã bán: {total}")
    if total == 0:
        print("Chưa có dữ liệu.")
        return
    for k, v in counters["tea"].items():
        pct = (v / total) * 100 if total else 0
        print(f"- {k:<10}: {v:>4} ly ({pct:6.2f}%)")

# ===== Main loop =====
def main():

    orders = load_orders()
    counters = load_counters()
    next_order_id = (max([o["order_id"] for o in orders], default=0) + 1)

    print("=== DỊCH VỤ ĐẶT TRÀ SỮA ===")
    while True:
        print("\nBắt đầu tạo đơn hàng mới...")
        drinks = []
        while True:

            drink = input_one_drink()
            drinks.append(drink)

            while True:
                action = input("\nChọn hành động: [a] thêm ly | [c] xác nhận đơn | [x] huỷ đơn > ").strip().lower()
                if action in ("a", "c", "x"):
                    break
                print("⛔ Vui lòng chọn a / c / x.")

            if action == "a":
                continue
            elif action == "x":
                print("❌ Đơn hàng đã huỷ.")
                drinks.clear()
                break
            else:  # confirm
                if not drinks:
                    print("⛔ Đơn rỗng, không thể xác nhận.")
                    continue
                # Cập nhật counters
                for d in drinks:
                    record_drink_to_counters(d, counters)
                save_counters(counters)

                order = build_order(next_order_id, drinks)
                orders.append(order)
                save_orders(orders)

                # In chi tiết đơn
                print("\n===== CHI TIẾT ĐƠN HÀNG =====")
                print_order(order)
                next_order_id += 1
                break

        cont = input("\nTiếp tục tạo đơn hàng mới? (y/n): ").strip().lower()
        if cont != "y":
            break

    # In số liệu cuối ngày
    print("\n===== SỐ LIỆU CUỐI NGÀY (COUNTERS) =====")
    print(json.dumps(counters, ensure_ascii=False, indent=2))
    analyze_tea(counters)

if __name__ == "__main__":
    main()
