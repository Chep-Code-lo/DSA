from __future__ import annotations
import json, os

# ===== Options =====
SIZE_OPTIONS    = ["30cm", "15cm"]
BREAD_OPTIONS   = ["white", "brown", "seeded"]
FILLING_OPTIONS = ["beef", "chicken", "cheese", "egg", "tuna", "turkey"]
SALAD_OPTIONS   = ["lettuce", "tomato", "sweetcorn", "cucumber", "peppers"]

# ===== Orders persistence =====
ORDERS_DATA_FILE = "orders_data.txt"

def load_orders():
    if os.path.exists(ORDERS_DATA_FILE):
        try:
            with open(ORDERS_DATA_FILE, "r", encoding="utf-8") as f:
                return json.load(f)
        except Exception:
            pass
    return []  # mặc định rỗng

def save_orders(orders):
    with open(ORDERS_DATA_FILE, "w", encoding="utf-8") as f:
        json.dump(orders, f, ensure_ascii=False, indent=2)

# ===== UI helpers =====
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

def choose_salads():
    print("\nChọn tối đa 3 loại salad (nhập số, cách nhau bởi dấu phẩy).")
    for i, s in enumerate(SALAD_OPTIONS, start=1):
        print(f"{i}. {s}")
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
                if not (1 <= v <= len(SALAD_OPTIONS)):
                    raise ValueError
                if v not in idxs:
                    idxs.append(v)
            if len(idxs) > 3:
                print("⛔ Tối đa 3 loại salad. Hãy chọn lại.")
                continue
            return [SALAD_OPTIONS[i-1] for i in idxs]
        except ValueError:
            print("⛔ Danh sách không hợp lệ. Hãy nhập lại theo mẫu 1,3 hoặc 2,4,5.")

# ===== Build one loaf (dict) =====
def input_one_loaf():
    size = choose_from_list("Chọn kích thước bánh mì:", SIZE_OPTIONS)
    bread = choose_from_list("Chọn loại bánh mì:", BREAD_OPTIONS)
    filling = choose_from_list("Chọn nhân bánh mì:", FILLING_OPTIONS)
    salads = choose_salads()
    loaf = {
        "size": size,
        "bread": bread,
        "filling": filling,
        "salads": salads,   # list length 0..3
    }
    print("\n✅ Ổ bánh đã chọn:")
    print(f"   - size:    {size}")
    print(f"   - bread:   {bread}")
    print(f"   - filling: {filling}")
    print(f"   - salads:  {', '.join(salads) if salads else '(none)'}")
    return loaf

# ===== Sales (re)compute from orders =====
def compute_sales_from_orders(orders):
    sales = {
        "size":    {k: 0 for k in SIZE_OPTIONS},
        "bread":   {k: 0 for k in BREAD_OPTIONS},
        "filling": {k: 0 for k in FILLING_OPTIONS},
    }
    for order in orders:
        for loaf in order.get("loaves", []):
            s = loaf.get("size")
            b = loaf.get("bread")
            f = loaf.get("filling")
            if s in sales["size"]:      sales["size"][s] += 1
            if b in sales["bread"]:     sales["bread"][b] += 1
            if f in sales["filling"]:   sales["filling"][f] += 1
    return sales

def analyze_fillings_from_orders(orders):
    sales = compute_sales_from_orders(orders)
    total = sum(sales["filling"].values())
    print("\n===== PHÂN TÍCH NHÂN =====")
    print(f"Tổng số baguette đã bán: {total}")
    if total == 0:
        print("Chưa có dữ liệu.")
        return
    perc = {k: (v / total) * 100 if total else 0.0 for k, v in sales["filling"].items()}
    most = max(perc, key=perc.get)
    least = min(perc, key=perc.get)
    for k in FILLING_OPTIONS:
        print(f"- {k:<8}: {sales['filling'][k]:>3} chiếc  ({perc[k]:6.2f}%)")
    print(f"\n🥇 Phổ biến nhất: {most} ({perc[most]:.2f}%)")
    print(f"🥉 Ít phổ biến nhất: {least} ({perc[least]:.2f}%)")

# ===== Orders analysis =====
def analyze_orders(orders):
    print("\n===== PHÂN TÍCH ĐƠN HÀNG =====")
    if not orders:
        print("Chưa có đơn hàng nào.")
        return

    total_orders = len(orders)
    total_loaves = sum(len(order["loaves"]) for order in orders)
    avg_loaves = total_loaves / total_orders if total_orders else 0

    # Đếm salad
    salad_counts = {s: 0 for s in SALAD_OPTIONS}
    for order in orders:
        for loaf in order["loaves"]:
            for s in loaf.get("salads", []):
                if s in salad_counts:
                    salad_counts[s] += 1

    most_salad = max(salad_counts, key=salad_counts.get) if salad_counts else None

    # Đếm filling
    filling_counts = {f: 0 for f in FILLING_OPTIONS}
    for order in orders:
        for loaf in order["loaves"]:
            f = loaf.get("filling")
            if f in filling_counts:
                filling_counts[f] += 1
    most_filling = max(filling_counts, key=filling_counts.get)

    print(f"Tổng số đơn hàng: {total_orders}")
    print(f"Tổng số ổ bánh: {total_loaves}")
    print(f"Trung bình mỗi đơn: {avg_loaves:.2f} ổ")

    if most_salad:
        print(f"🥗 Salad được chọn nhiều nhất: {most_salad} ({salad_counts[most_salad]} lần)")
    print(f"🥪 Nhân phổ biến nhất: {most_filling} ({filling_counts[most_filling]} lần)")

# ===== Print order =====
def print_order(order_id, loaves):
    print("\n===== CHI TIẾT ĐƠN HÀNG =====")
    print(f"Mã đơn hàng: {order_id}")
    for i, loaf in enumerate(loaves, start=1):
        salads_str = ", ".join(loaf["salads"]) if loaf["salads"] else "(none)"
        print(f"  Ổ #{i}: size={loaf['size']}, bread={loaf['bread']}, filling={loaf['filling']}, salads=[{salads_str}]")

# ===== Main loop =====
def main():
    orders = []
    save_orders(orders)
    next_order_id = 1
    print("=== DỊCH VỤ ĐẶT BAGUETTE (NO-OOP) ===")
    while True:
        print("\nBắt đầu tạo đơn hàng mới...")
        loaves = []

        while True:
            loaf = input_one_loaf()
            loaves.append(loaf)

            while True:
                action = input("\nChọn hành động: [a] thêm ổ  |  [c] xác nhận đơn  |  [x] huỷ đơn  > ").strip().lower()
                if action in ("a", "c", "x"):
                    break
                print("⛔ Vui lòng chọn a / c / x.")

            if action == "a":
                continue
            elif action == "x":
                print("❌ Đơn hàng đã huỷ.")
                loaves.clear()
                break
            else:  # confirm
                if not loaves:
                    print("⛔ Đơn rỗng, không thể xác nhận.")
                    continue

                # TẠO và LƯU ĐƠN HÀNG
                order = {"id": next_order_id, "loaves": loaves}
                orders.append(order)
                save_orders(orders)

                print_order(next_order_id, loaves)
                print("✅ Đơn hàng đã được xác nhận.")
                next_order_id += 1
                break
        cont = input("\nTiếp tục tạo đơn hàng mới? (y/n): ").strip().lower()
        if cont != "y":
            break

    # ===== BÁO CÁO CUỐI CHƯƠNG TRÌNH =====
    print("\n===== SỐ LIỆU BÁN HÀNG =====")
    sales = compute_sales_from_orders(orders)
    print(json.dumps(sales, ensure_ascii=False, indent=2))
    analyze_fillings_from_orders(orders)
    analyze_orders(orders)

if __name__ == "__main__":
    main()
