#!/usr/bin/env python3

def is_numeric(ch): return ch in set("0123456789")
def is_whitespace(ch): return ch == ' '
def is_open_paren(ch): return ch == '('
def is_close_paren(ch): return ch == ')'

def read_num(s):
    i = 0
    while i < len(s):
        if not is_numeric(s[i]):
            break
        i = i + 1
    return (int(s[:i]), s[i:])

def read_one(s):
    """
    > read_one("123 456 789")
    (123, ' 456 789')

    > read_one("(123 456) 789")
    ([123, 456], ' 789')
    """
    i = 0
    while i < len(s) and is_whitespace(s[i]):
        i = i + 1

    if i == len(s):
        return None, ""

    if is_numeric(s[i]):
        return read_num(s[i:])

    if is_open_paren(s[i]):
        return read_list(s[i+1:])

    raise ValueError(f"Cannot tokenize character: {s[i]}")

def read_list(s):
    """
    > read_list("123 456)")
    ([123, 456], '')
    """
    tokens = []

    while s != "" and s[0] != ')':
        token, s = read_one(s)
        if token == None:
            raise ValueError("Unbalanced parens")
        tokens.append(token)

    if s == "" or s[0] != ')':
        raise ValueError("Unbalanced parens")

    return tokens, s[1:]

def read_all(s):
    """
    > read_all("123 3455     ")
    [123, 3455]
    """
    tokens = []

    while s != "":
        token, s = read_one(s)
        if token == None:
            break
        tokens.append(token)

    return tokens
