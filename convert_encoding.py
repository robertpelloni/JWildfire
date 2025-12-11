
try:
    with open('src/js/glsl/glslFuncRunner.java', 'r', encoding='iso-8859-1') as f:
        content = f.read()
    
    with open('src/js/glsl/glslFuncRunner.java', 'w', encoding='utf-8') as f:
        f.write(content)
    print("Conversion successful")
except Exception as e:
    print(f"Conversion failed: {e}")
