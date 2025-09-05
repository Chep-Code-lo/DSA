# baguette_all_in_one.py
# ------------------------------------
# Unified script combining:
#  - Order logging (sales_data.txt) with full loaf details
#  - Sales counters (sales_counters.json) for size/bread/filling analytics
#  - Simple interactive CLI (no OOP) to build and confirm orders
#
# How to run:
#   python baguette_all_in_one.py
#
# Files:
#   - sales_data.txt      : JSON list of orders [{order_id, loaves, status}]
#   - sales_counters.json : JSON counters {"size": {...}, "bread": {...}, "filling": {...}}
#
# A pretty-print output example after confirming an order:
# Mã đơn hàng: 1
#   Ổ #1: size=30cm, bread=white, filling=beef, salads=[lettuce, tomato, sweetcorn]
#   Ổ #2: size=15cm, bread=brown, filling=chicken, salads=[tomato, sweetcorn, cucumber]
# ✅ Đơn hàng đã được xác nhận.

from __future__ import annotations
import json, os

# ===== Options =====
SIZE_OPTIONS    = ["30cm", "15cm"]
BREAD_OPTIONS   = ["white", "brown", "seeded"]
FILLING_OPTIONS = ["beef", "chicken", "cheese", "egg", "tuna", "turkey"]
SALAD_OPTIONS   = ["lettuce", "tomato", "sweetcorn", "cucumber", "peppers", "onion", "pickles"]

# ===== Files =====
ORDERS_LOG_FILE = "sales_data.txt"        # list of orders
COUNTERS_FILE   = "sales_counters.json"   # counters for stats

# ===== Default counters =====
#Tạo cấu trúc bộ đếm cho các nhóm "size", "bread", "filling"
def _default_counters():
    return {
        "size":    {k: 0 for k in SIZE_OPTIONS},
        "bread":   {k: 0 for k in BREAD_OPTIONS},
        "filling": {k: 0 for k in FILLING_OPTIONS},
    }

# ===== Persistence: orders =====
#Đọc file và kiểm tra file
def load_orders():
    if os.path.exists(ORDERS_LOG_FILE):
        try:
            with open(ORDERS_LOG_FILE, "r", encoding="utf-8") as f:
                data = json.load(f)
            return data if isinstance(data, list) else []
        except Exception:
            return []
    return []
#Lưu file và ghi toàn bộ danh sách orders vô file dưới dạng json
def save_orders(orders):
    with open(ORDERS_LOG_FILE, "w", encoding="utf-8") as f:
        json.dump(orders, f, ensure_ascii=False, indent=2)

# ===== Persistence: counters =====
#Đọc file sales_counters.json
def load_counters():
    if os.path.exists(COUNTERS_FILE):
        try:
            with open(COUNTERS_FILE, "r", encoding="utf-8") as f:
                data = json.load(f)
            # ensure all groups/keys exist
            base = _default_counters()
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
#Ghi bộ đếm counters vô file sales_counters.json
def save_counters(counters):
    with open(COUNTERS_FILE, "w", encoding="utf-8") as f:
        json.dump(counters, f, ensure_ascii=False, indent=2)

# ===== UI helpers =====
#Hiển thị danh sách các lựa chọn và nhập vào bàn phím lựa chọn, kiểm tra xem hợp lệ hay không và yêu cầu nhập lại khi không hợp lệ
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
#Hiện thị danh sách lựa chọn salads và chọn salads theo đúng yêu cầu ( Nếu sai thì chọn lại)
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

# ===== Build loaf & order =====
#Xây dựng đơn hàng và in ra các thông tin đã chọn
def input_one_loaf():
    size = choose_from_list("Chọn kích thước bánh mì:", SIZE_OPTIONS)
    bread = choose_from_list("Chọn loại bánh mì:", BREAD_OPTIONS)
    filling = choose_from_list("Chọn nhân bánh mì:", FILLING_OPTIONS)
    salads = choose_salads()
    loaf = {"size": size, "bread": bread, "filling": filling, "salads": salads}
    print("\n✅ Ổ bánh đã chọn:")
    print(f"   - size:    {size}")
    print(f"   - bread:   {bread}")
    print(f"   - filling: {filling}")
    print(f"   - salads:  {', '.join(salads) if salads else '(none)'}")
    return loaf
#fomat lại file json cho đẹp và đúng yêu cầu trả về một dict đơn hàng
def build_order(order_id, loaves):
    return {"order_id": order_id, "loaves": loaves, "status": "confirmed"}

# ===== Update counters =====
# cập nhật lại bộ đếm
def record_loaf_to_counters(loaf, counters):
    size = loaf.get("size")
    bread = loaf.get("bread")
    filling = loaf.get("filling")
    if size in counters["size"]:
        counters["size"][size] += 1
    if bread in counters["bread"]:
        counters["bread"][bread] += 1
    if filling in counters["filling"]:
        counters["filling"][filling] += 1

# ===== Pretty printing =====
#Hiển thị mã đơn hàng đã chọn , danh sách các ổ
def print_order(order):
    print(f"Mã đơn hàng: {order['order_id']}")
    for i, loaf in enumerate(order["loaves"], start=1):
        salads_str = ", ".join(loaf.get("salads", [])) if loaf.get("salads") else "(none)"
        print(f"  Ổ #{i}: size={loaf.get('size')}, bread={loaf.get('bread')}, "
              f"filling={loaf.get('filling')}, salads=[{salads_str}]")
    if order.get("status") == "confirmed":
        print("✅ Đơn hàng đã được xác nhận.")
#Phân tích số lượng baguette đã bán và in ra tổng số bánh, tỉ lệ phần trăm
def analyze_fillings(counters):
    print("\n===== PHÂN TÍCH NHÂN =====")
    total = sum(counters["filling"].values())
    print(f"Tổng số baguette đã bán: {total}")
    if total == 0:
        print("Chưa có dữ liệu.")
        return
    for k, v in counters["filling"].items():
        pct = (v / total) * 100 if total else 0
        print(f"- {k:<8}: {v:>4} chiếc ({pct:6.2f}%)")

# ===== Main loop =====

def main():
    #Tải đơn hàng
    orders = load_orders()
    counters = load_counters()
    next_order_id = (max([o["order_id"] for o in orders], default=0) + 1)

    print("=== DỊCH VỤ ĐẶT BAGUETTE (ALL-IN-ONE) ===")
    while True:
        print("\nBắt đầu tạo đơn hàng mới...")
        loaves = []
        while True:
            loaf = input_one_loaf()
            loaves.append(loaf)
            #xác nhận những lựa chọn
            while True:
                action = input("\nChọn hành động: [a] thêm ổ | [c] xác nhận đơn | [x] huỷ đơn > ").strip().lower()
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
                # Update counters
                for lf in loaves:
                    record_loaf_to_counters(lf, counters)
                save_counters(counters)

                # Append order to log
                order = build_order(next_order_id, loaves)
                orders.append(order)
                save_orders(orders)

                # Print nicely
                print("\n===== CHI TIẾT ĐƠN HÀNG =====")
                print_order(order)
                next_order_id += 1
                break

        cont = input("\nTiếp tục tạo đơn hàng mới? (y/n): ").strip().lower()
        if cont != "y":
            break
    #In ra số liệu thống kê cuối ngày
    # End-of-session summary
    print("\n===== SỐ LIỆU CUỐI NGÀY (COUNTERS) =====")
    print(json.dumps(counters, ensure_ascii=False, indent=2))
    analyze_fillings(counters)

if __name__ == "__main__":
    main()
