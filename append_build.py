
# Part 2 appending and writing
with open('/build_courses_json.py', 'a') as f:
    f.write('''
with open("app/src/main/assets/courses_final_firebase.json", "w", encoding="utf-8") as f_out:
    json.dump(p1, f_out, ensure_ascii=False, indent=2)

with open("app/src/main/assets/courses.json", "w", encoding="utf-8") as f_out:
    json.dump(p1, f_out, ensure_ascii=False, indent=2)

print("Generated exactly", len(p1), "courses in assets with authentic titleKn!")
''')
