#!/usr/bin/env python3
"""Genera un PDF con texto enriquecido a partir de programacion-funcional.md"""
import markdown
from fpdf import FPDF
from fpdf.fonts import FontFace

MD_PATH = "backend/docs/programacion-funcional.md"
PDF_PATH = "backend/docs/programacion-funcional.pdf"

COLOR_PRIMARY = (0, 51, 102)
COLOR_ACCENT = (0, 102, 153)
COLOR_TEXT = (51, 51, 51)
COLOR_CODE_BG = (240, 244, 248)
COLOR_TABLE_HEADER = (0, 51, 102)
COLOR_TABLE_HEADER_TEXT = (255, 255, 255)
COLOR_TABLE_ALT = (245, 247, 250)


class PDF(FPDF):
    def __init__(self):
        super().__init__()
        self.set_auto_page_break(auto=True, margin=20)
        self.add_font("DejaVu", "", "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf")
        self.add_font("DejaVu", "B", "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf")
        self.add_font("DejaVuMono", "", "/usr/share/fonts/truetype/dejavu/DejaVuSansMono.ttf")
        self.add_font("DejaVuMono", "B", "/usr/share/fonts/truetype/dejavu/DejaVuSansMono-Bold.ttf")

    def header(self):
        if self.page_no() > 1:
            self.set_font("DejaVu", "", 8)
            self.set_text_color(*COLOR_ACCENT)
            self.cell(0, 8, "Paradigma de programación funcional en el backend", align="R")
            self.ln(4)
            self.set_draw_color(*COLOR_ACCENT)
            self.set_line_width(0.3)
            self.line(10, self.get_y(), 200, self.get_y())
            self.ln(4)

    def footer(self):
        self.set_y(-15)
        self.set_font("DejaVu", "", 8)
        self.set_text_color(150, 150, 150)
        self.cell(0, 10, f"Pagina {self.page_no()}", align="C")


def main():
    with open(MD_PATH, "r", encoding="utf-8") as f:
        md_text = f.read()

    # Convertir Markdown a HTML
    html = markdown.markdown(
        md_text,
        extensions=["tables", "fenced_code", "nl2br"],
    )

    pdf = PDF()
    pdf.add_page()
    pdf.set_left_margin(15)
    pdf.set_right_margin(15)
    pdf.set_font("DejaVu", "", 10)
    pdf.set_text_color(*COLOR_TEXT)

    # Estilos por etiqueta para write_html
    tag_styles = {
        "h1": FontFace(color=COLOR_PRIMARY, size_pt=20, emphasis="BOLD"),
        "h2": FontFace(color=COLOR_ACCENT, size_pt=15, emphasis="BOLD"),
        "h3": FontFace(color=COLOR_PRIMARY, size_pt=12, emphasis="BOLD"),
        "pre": FontFace(family="DejaVuMono", size_pt=8.5),
        "code": FontFace(family="DejaVuMono", color=COLOR_ACCENT, size_pt=9.5),
        "li": FontFace(size_pt=10),
        "p": FontFace(size_pt=10),
    }

    pdf.write_html(html, tag_styles=tag_styles)
    pdf.output(PDF_PATH)
    print(f"PDF generado: {PDF_PATH}")


if __name__ == "__main__":
    main()