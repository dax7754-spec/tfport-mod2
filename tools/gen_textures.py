"""Генерирует оригинальные текстуры-заглушки для мода.

Запуск из корня проекта:  python3 tools/gen_textures.py
Можно заменить любой PNG своим файлом с тем же именем.
"""
from pathlib import Path
from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parent.parent / "src/main/resources/assets/tfport/textures"
ITEM_DIR = ROOT / "item"
ENTITY_DIR = ROOT / "entity"
ITEM_DIR.mkdir(parents=True, exist_ok=True)
ENTITY_DIR.mkdir(parents=True, exist_ok=True)

OUTLINE = (30, 30, 34, 255)
STEEL = (130, 136, 146, 255)
DARK_STEEL = (74, 78, 88, 255)
WOOD = (150, 104, 62, 255)
DARK_WOOD = (102, 68, 40, 255)
OLIVE = (98, 120, 52, 255)
WHITE = (236, 238, 240, 255)
RED = (190, 48, 44, 255)
BREAD = (214, 170, 96, 255)


def new_icon():
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    return img, ImageDraw.Draw(img)


def save_icon(img, name):
    img.save(ITEM_DIR / f"{name}.png")


def scattergun():
    img, d = new_icon()
    d.line([(2, 12), (13, 5)], fill=OUTLINE, width=3)
    d.line([(5, 10), (14, 4)], fill=STEEL, width=1)
    d.line([(2, 12), (6, 10)], fill=WOOD, width=2)
    d.rectangle([5, 11, 6, 13], fill=DARK_WOOD)
    save_icon(img, "scattergun")


def bat():
    img, d = new_icon()
    d.line([(2, 14), (13, 3)], fill=OUTLINE, width=3)
    d.line([(2, 14), (13, 3)], fill=WOOD, width=1)
    d.line([(9, 7), (13, 3)], fill=DARK_WOOD, width=2)
    save_icon(img, "bat")


def rocket_launcher():
    img, d = new_icon()
    d.rectangle([2, 6, 14, 10], fill=OUTLINE)
    d.rectangle([3, 7, 13, 9], fill=OLIVE)
    d.rectangle([1, 5, 3, 11], fill=DARK_STEEL)
    d.rectangle([12, 5, 14, 11], fill=DARK_STEEL)
    d.rectangle([6, 10, 8, 13], fill=DARK_WOOD)
    save_icon(img, "rocket_launcher")


def equalizer():
    img, d = new_icon()
    d.line([(2, 14), (11, 5)], fill=OUTLINE, width=3)
    d.line([(2, 14), (11, 5)], fill=DARK_WOOD, width=1)
    d.rectangle([9, 1, 14, 6], fill=OUTLINE)
    d.rectangle([10, 2, 13, 5], fill=STEEL)
    save_icon(img, "equalizer")


def minigun():
    img, d = new_icon()
    d.rectangle([1, 4, 7, 12], fill=OUTLINE)
    d.rectangle([2, 5, 6, 11], fill=DARK_STEEL)
    for y in (5, 7, 9):
        d.rectangle([7, y, 15, y + 1], fill=OUTLINE)
        d.line([(8, y), (14, y)], fill=STEEL)
    d.rectangle([3, 12, 5, 14], fill=DARK_WOOD)
    save_icon(img, "minigun")


def sandvich():
    img, d = new_icon()
    d.rectangle([2, 4, 13, 12], fill=OUTLINE)
    d.rectangle([3, 5, 12, 6], fill=BREAD)
    d.rectangle([3, 7, 12, 7], fill=(120, 176, 70, 255))
    d.rectangle([3, 8, 12, 9], fill=(170, 70, 60, 255))
    d.rectangle([3, 10, 12, 11], fill=BREAD)
    save_icon(img, "sandvich")


def medigun():
    img, d = new_icon()
    d.rectangle([2, 5, 11, 11], fill=OUTLINE)
    d.rectangle([3, 6, 10, 10], fill=WHITE)
    d.rectangle([6, 6, 7, 10], fill=RED)
    d.rectangle([4, 8, 9, 8], fill=RED)
    d.rectangle([11, 7, 15, 9], fill=OUTLINE)
    d.rectangle([12, 8, 14, 8], fill=STEEL)
    save_icon(img, "medigun")


def bonesaw():
    img, d = new_icon()
    d.line([(2, 13), (6, 9)], fill=OUTLINE, width=3)
    d.line([(2, 13), (6, 9)], fill=DARK_WOOD, width=1)
    d.polygon([(6, 9), (14, 2), (15, 5), (8, 11)], fill=OUTLINE)
    d.polygon([(7, 9), (13, 3), (14, 5), (8, 10)], fill=STEEL)
    save_icon(img, "bonesaw")


# ---- Скины ботов: 64x64, используется только верхняя часть раскладки гуманоида ----
CLASS_COLORS = {
    #  кожа,            одежда,            штаны,            головной убор или None
    "scout":   ((232, 190, 150), (64, 150, 214), (70, 70, 80), (64, 150, 214)),
    "soldier": ((214, 170, 130), (96, 120, 56), (60, 70, 44), (110, 116, 124)),
    "heavy":   ((226, 180, 140), (170, 52, 46), (52, 44, 44), None),
    "medic":   ((236, 200, 170), (236, 238, 240), (92, 100, 112), None),
}


def rect(d, box, color):
    d.rectangle([box[0], box[1], box[2] - 1, box[3] - 1], fill=color)


def bot_skin(name, skin, shirt, pants, hat):
    img = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    rect(d, (0, 0, 32, 16), skin + (255,))          # голова
    rect(d, (8, 12, 16, 16), (90, 62, 44, 255)) if name == "heavy" else None
    rect(d, (9, 10, 11, 11), (30, 30, 34, 255))     # глаза
    rect(d, (13, 10, 15, 11), (30, 30, 34, 255))
    rect(d, (16, 16, 40, 32), shirt + (255,))       # тело
    rect(d, (0, 16, 16, 32), pants + (255,))        # ноги
    rect(d, (40, 16, 56, 32), shirt + (255,))       # руки
    rect(d, (40, 29, 56, 32), skin + (255,))        # кисти
    if hat is not None:                             # слой «шапки»
        rect(d, (40, 0, 48, 8), hat + (255,))       # верх
        rect(d, (32, 8, 64, 11), hat + (255,))      # боковые грани
    img.save(ENTITY_DIR / f"bot_{name}.png")


if __name__ == "__main__":
    for fn in (scattergun, bat, rocket_launcher, equalizer, minigun, sandvich, medigun, bonesaw):
        fn()
    for cls, (skin, shirt, pants, hat) in CLASS_COLORS.items():
        bot_skin(cls, skin, shirt, pants, hat)
    print("Готово:", ROOT)
