#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Google Forms random filler + resubmitter (ROBUST)

- Auto-detects question types per page (text, radio, checkbox).
- Fills the FIRST text field with a random HCMC/Southern VN university.
- Picks one random option for each radio group.
- Picks a random non-empty subset for each checkbox group, EXCLUDING the last option (often “Other”).
- Walks multi-section forms (Next/Tiếp) until Submit (Gửi).
- After submit, finds “Submit another response” (VN/EN variants) or builds confirm URL and loops.
"""

import argparse
import random
import time
import os
import re

from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys

from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

from selenium.common.exceptions import TimeoutException

# -------------------- CONFIG --------------------
FORM_URL = ("https://docs.google.com/forms/d/e/"
            "1FAIpQLScAxUt2d-sTfGoVyI3JZ4vaXoZWSo2QBeuPUg_OUnP09h3MDg/viewform"
            "?fbclid=IwY2xjawMzXxRleHRuA2FlbQIxMQABHjGXjRWxy9Ke0SQf4kdvnXEgqt--2IijkDKIwfPSFciW9r_xCsgr78t5nSw1_aem_VrsY4GeG67VXGCv4ZEJRSw")

# Southern VN / HCMC universities
HCMC_UNIVERSITIES = [
    "Đại học Bách Khoa TP.HCM (HCMUT)",
    "Đại học Khoa học Tự nhiên – ĐHQG-HCM",
    "Đại học Khoa học Xã hội & Nhân văn – ĐHQG-HCM",
    "Đại học Công nghệ Thông tin – ĐHQG-HCM",
    "Đại học Kinh tế TP.HCM (UEH)",
    "Đại học Ngân hàng TP.HCM (HUB)",
    "Đại học Sư phạm TP.HCM (HCMUE)",
    "Đại học Sài Gòn (SGU)",
    "Đại học Tôn Đức Thắng (TDTU)",
    "Đại học Hoa Sen (HSU)",
    "Đại học Mở TP.HCM (HCMOU)",
    "Đại học Tài chính – Marketing (UFM)",
    "Đại học FPT TP.HCM",
    "Đại học Văn Lang (VLU)",
    "Đại học Nguyễn Tất Thành (NTTU)",
    "Đại học Quốc tế – ĐHQG-HCM (HCMIU)",
    "Đại học Nông Lâm TP.HCM (NLU)",
    "RMIT Vietnam – Cơ sở Nam Sài Gòn",
]

# -------------------- Driver & Waits --------------------
def make_driver(headless: bool, window_size="1280,900"):
    opts = webdriver.ChromeOptions()
    if headless:
        opts.add_argument("--headless=new")
    opts.add_argument(f"--window-size={window_size}")
    opts.add_argument("--no-sandbox")
    opts.add_argument("--disable-dev-shm-usage")
    # user-agent nhẹ để tránh một số block ngớ ngẩn
    opts.add_argument("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                      "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127 Safari/537.36")
    svc = Service(ChromeDriverManager().install())
    return webdriver.Chrome(service=svc, options=opts)

def wait_present(driver, xpath, timeout):
    return WebDriverWait(driver, timeout).until(EC.presence_of_element_located((By.XPATH, xpath)))

def wait_clickable(driver, xpath, timeout):
    return WebDriverWait(driver, timeout).until(EC.element_to_be_clickable((By.XPATH, xpath)))

# -------------------- Page Helpers --------------------
def page_question_containers(driver):
    # Current page’s question blocks
    return driver.find_elements(By.XPATH, "//div[@role='list']//div[contains(@jsmodel,'CP1oW')]")

def find_submit_button(driver):
    xpath = ("//div[@role='button']//span[normalize-space(text())='Gửi']"
             "|//div[@role='button']//span[normalize-space(text())='Submit']")
    buttons = driver.find_elements(By.XPATH, xpath)
    return buttons[0] if buttons else None

def find_next_button(driver):
    # VN + EN variants
    xpath = ("//div[@role='button']//span[normalize-space(.)='Tiếp']"
             "|//div[@role='button']//span[contains(normalize-space(.), 'Tiếp theo')]"
             "|//div[@role='button']//span[normalize-space(.)='Tiếp tục']"
             "|//div[@role='button']//span[normalize-space(.)='Next']")
    buttons = driver.find_elements(By.XPATH, xpath)
    return buttons[0] if buttons else None

# -------------------- Question Type Helpers --------------------
def get_radios(container):
    return container.find_elements(By.XPATH, ".//div[@role='radio']")

def get_checkboxes(container):
    # Classic + list-style variants
    return container.find_elements(
        By.XPATH,
        ".//div[@role='checkbox']"
        " | .//div[@role='list']//div[@role='listitem' and @aria-checked]"
    )

def get_text_inputs(container):
    return container.find_elements(By.XPATH, ".//input[@type='text' and contains(@class,'whsOnd')]")

def get_textareas(container):
    return container.find_elements(By.XPATH, ".//textarea[contains(@class,'KHxj8b')]")

# -------------------- Fillers --------------------
def fill_text_field(el, value, pause):
    el.location_once_scrolled_into_view
    time.sleep(pause)
    el.clear()
    el.send_keys(value)
    el.send_keys(Keys.TAB)
    time.sleep(pause)

def select_random_radio_in_container(container, pause):
    radios = get_radios(container)
    if not radios:
        return False

    # 🚫 filter out "Mục khác" / "Other"
    radios = [r for r in radios if "Mục khác" not in r.text and "Other" not in r.text]
    if not radios:
        return False

    choice = random.choice(radios)
    choice.location_once_scrolled_into_view
    time.sleep(pause)
    choice.click()
    time.sleep(pause)
    return True

def select_random_checkboxes_in_container(container, pause, min_k=1, max_k=None, exclude_last=True):
    boxes = get_checkboxes(container)
    if not boxes:
        return False

    # 🚫 filter out "Mục khác" / "Other"
    boxes = [b for b in boxes if "Mục khác" not in b.text and "Other" not in b.text]

    # Also exclude last if configured
    if exclude_last and len(boxes) > 1:
        boxes = boxes[:-1]

    if not boxes:
        return False

    n = len(boxes)
    lo = max(1, min_k) if min_k else 1
    hi = n if max_k is None else min(n, max_k)
    k = random.randint(lo, hi)

    random.shuffle(boxes)
    picks = boxes[:k]
    for b in picks:
        b.location_once_scrolled_into_view
        time.sleep(pause)
        b.click()
        time.sleep(pause)

    return True

def is_checked(el):
    v = el.get_attribute("aria-checked")
    if v is not None:
        return v.lower() == "true"
    v = el.get_attribute("aria-pressed")
    return (v or "").lower() == "true"

def fill_current_page(driver, pause, filled_university_flag):
    containers = page_question_containers(driver)
    for c in containers:
        # Fill the first text field encountered with a random university
        if not filled_university_flag:
            inputs = get_text_inputs(c)
            if inputs:
                fill_text_field(inputs[0], random.choice(HCMC_UNIVERSITIES), pause)
                filled_university_flag = True
                continue
            tas = get_textareas(c)
            if tas:
                fill_text_field(tas[0], random.choice(HCMC_UNIVERSITIES), pause)
                filled_university_flag = True
                continue
        # Radios
        if select_random_radio_in_container(c, pause):
            continue
        # Checkboxes
        select_random_checkboxes_in_container(c, pause)
    return filled_university_flag

# -------------------- Submit / Resubmit --------------------
def _try_find_submit_elements(driver):
    # Multiple strategies to locate the submit button
    xpaths = [
        "//div[@role='button']//span[normalize-space(.)='Gửi']/ancestor::div[@role='button']",
        "//div[@role='button']//span[normalize-space(.)='Submit']/ancestor::div[@role='button']",
        "//div[@role='button' and (@aria-label='Gửi' or @aria-label='Submit')]",
        "//div[contains(@class,'lRwqcd')]//div[@role='button']//*[normalize-space(.)='Gửi']/ancestor::div[@role='button']",
        "//div[contains(@class,'lRwqcd')]//div[@role='button']//*[normalize-space(.)='Submit']/ancestor::div[@role='button']",
    ]
    for xp in xpaths:
        els = driver.find_elements(By.XPATH, xp)
        if els:
            return els[0]
    return None

def submit_form(driver, pause, max_wait):
    # Ensure footer is in view
    driver.execute_script("window.scrollTo(0, document.body.scrollHeight);")
    time.sleep(pause)

    btn = _try_find_submit_elements(driver)
    if not btn:
        try:
            WebDriverWait(driver, max_wait).until(EC.presence_of_element_located((By.XPATH, "//form")))
        except Exception:
            pass
        driver.execute_script("window.scrollTo(0, document.body.scrollHeight);")
        time.sleep(pause)
        btn = _try_find_submit_elements(driver)

    if not btn:
        try:
            btn = wait_clickable(driver,
                "//div[@role='button']//span[normalize-space(text())='Gửi']|"
                "//div[@role='button']//span[normalize-space(text())='Submit']",
                max_wait)
        except Exception:
            btn = None

    if btn:
        try:
            driver.execute_script("arguments[0].scrollIntoView({block:'center'});", btn)
            time.sleep(pause)
            btn.click()
        except Exception:
            driver.execute_script("arguments[0].click();", btn)
    else:
        # Final fallback: press Enter on the form
        try:
            form = driver.find_element(By.XPATH, "//form")
            form.send_keys(Keys.ENTER)
        except Exception:
            raise TimeoutException("Could not locate or click the Submit/Gửi button.")

    # Wait for confirmation/redirect
    try:
        WebDriverWait(driver, 15).until(EC.url_matches(r".*(formResponse|viewform\?edit_requested).*"))
    except TimeoutException:
        time.sleep(4)

def click_next(driver, pause, max_wait):
    btn = wait_clickable(driver,
                         "//div[@role='button']//span[normalize-space(.)='Tiếp']|"
                         "//div[@role='button']//span[contains(normalize-space(.), 'Tiếp theo')]|"
                         "//div[@role='button']//span[normalize-space(.)='Tiếp tục']|"
                         "//div[@role='button']//span[normalize-space(.)='Next']",
                         max_wait)
    btn.location_once_scrolled_into_view
    time.sleep(pause)
    btn.click()
    time.sleep(pause)

def _switch_to_last_window_if_new_opened(driver):
    try:
        if len(driver.window_handles) > 1:
            driver.switch_to.window(driver.window_handles[-1])
    except Exception:
        pass

def _try_in_all_iframes(driver, fn_try):
    # thử trong default
    driver.switch_to.default_content()
    if fn_try():
        return True
    # thử trong mọi iframe
    iframes = driver.find_elements(By.TAG_NAME, "iframe")
    for fr in iframes:
        try:
            driver.switch_to.default_content()
            driver.switch_to.frame(fr)
            if fn_try():
                return True
        except Exception:
            continue
    driver.switch_to.default_content()
    return False

def click_submit_another_response(driver, pause, max_wait, verbose=False):
    """
    Robust resubmit:
    1) Try known link/button texts (VN + EN + variants) in default & iframes
    2) Try anchor with href '*viewform*'
    3) If still not found, synthesize URL from current 'formResponse' -> 'viewform?usp=form_confirm'
    """
    _switch_to_last_window_if_new_opened(driver)
    time.sleep(pause)

    variants = [
        "Gửi câu trả lời khác",
        "Gửi phản hồi khác",
        "Gửi ý kiến phản hồi khác",
        "Gửi phản hồi mới",
        "Trả lời lại",
        "Submit another response",
        "Submit another form",
    ]

    def try_text_locators():
        for text in variants:
            try:
                el = WebDriverWait(driver, 1.5).until(
                    EC.presence_of_element_located(
                        (By.XPATH,
                         f"//a[contains(normalize-space(.), '{text}')]"
                         f"|//span[contains(normalize-space(.), '{text}')]"
                         f"|//div[contains(normalize-space(.), '{text}')]"
                         )
                    )
                )
                driver.execute_script("arguments[0].scrollIntoView({block:'center'});", el)
                time.sleep(pause)
                if verbose:
                    try:
                        print(f"[INFO] Resubmit element found: '{el.text.strip()}'")
                    except Exception:
                        pass
                try:
                    el.click()
                except Exception:
                    driver.execute_script("arguments[0].click();", el)
                WebDriverWait(driver, max_wait).until(EC.url_matches(r".*(viewform).*"))
                return True
            except Exception:
                continue
        return False

    # 1) thử theo text ở default + iframes
    if _try_in_all_iframes(driver, try_text_locators):
        return

    # 2) thử theo href viewform (default + iframes)
    def try_href_locator():
        try:
            el = driver.find_element(By.XPATH, "//a[contains(@href,'viewform')]")
            driver.execute_script("arguments[0].scrollIntoView({block:'center'});", el)
            time.sleep(pause)
            if verbose:
                print(f"[INFO] Resubmit href: {el.get_attribute('href')}")
            try:
                el.click()
            except Exception:
                driver.execute_script("arguments[0].click();", el)
            WebDriverWait(driver, max_wait).until(EC.url_matches(r".*(viewform).*"))
            return True
        except Exception:
            return False

    if _try_in_all_iframes(driver, try_href_locator):
        return

    # 3) Fallback: tự build URL
    cur = driver.current_url
    if "formResponse" in cur:
        direct = cur.replace("formResponse", "viewform?usp=form_confirm")
        if verbose:
            print(f"[INFO] No resubmit link found; navigating directly: {direct}")
        driver.get(direct)
        WebDriverWait(driver, max_wait).until(EC.url_matches(r".*(viewform).*"))
        return

    # Debug dump (tùy chọn)
    ts = int(time.time())
    try:
        with open(f"debug_submit_{ts}.html", "w", encoding="utf-8") as f:
            f.write(driver.page_source)
        driver.save_screenshot(f"debug_submit_{ts}.png")
        if verbose:
            print(f"[INFO] Saved debug HTML & PNG with timestamp {ts}")
    except Exception:
        pass

    raise TimeoutException("Could not find or open the 'Submit another response' link.")

# -------------------- Main Loop --------------------
def run(iterations, delay, max_wait, show):
    headless = not show
    driver = make_driver(headless=headless)
    try:
        for i in range(iterations):
            driver.get(FORM_URL)
            wait_present(driver, "//form", max_wait)

            filled_uni = False

            # Fill across pages until submit is present
            while True:
                filled_uni = fill_current_page(driver, delay, filled_uni)
                submit_btn = find_submit_button(driver)
                if submit_btn:
                    break
                nxt = find_next_button(driver)
                if nxt:
                    click_next(driver, delay, max_wait)
                    wait_present(driver, "//form", max_wait)
                else:
                    break

            submit_form(driver, delay, max_wait)
            print(f"[INFO] Submitted #{i+1}")

            if i < iterations - 1:
                click_submit_another_response(driver, delay, max_wait, verbose=True)
                print("[INFO] Back to form")

    finally:
        driver.quit()

def main():
    parser = argparse.ArgumentParser(description="Robust Google Forms filler/resubmitter — Bot #2")
    parser.add_argument("--iterations", type=int, default=10, help="Number of submissions")
    parser.add_argument("--delay", type=float, default=0.05, help="Pause between actions (seconds)")
    parser.add_argument("--max-wait", type=float, default=12, help="Explicit wait timeout (seconds)")
    parser.add_argument("--show", action="store_true", help="Show browser (disables headless)")
    args = parser.parse_args()
    run(args.iterations, args.delay, args.max_wait, args.show)

if __name__ == "__main__":
    main()
