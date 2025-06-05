import re
import string

def remove_special_characters(text):
    # Define a regex pattern for non-ASCII characters
    pattern = re.compile(r'[^\x00-\x7F]+')
    # Replace non-ASCII characters with an empty string
    cleaned_text = pattern.sub('', text)
    return cleaned_text

def process_input_with_apostrophe(text):
    if "'" in text:
        text = text.replace("'", "''")
    return text