// https://ziglearn.org/ chapter 1

////////////////////////////////////////////////////////////////////////////////
// ASSIGNMENT

const std = @import("std");

pub fn main() void {
    // one();
    two();
}

fn one() void {
    std.debug.print("Hello, {s}!\n", .{"WORLD"});
}

const five: i32 = 5;
var counter: u32 = 5000;

// type coercion with @as

const inferred_constant = @as(i32, 5);
var inferred_var = @as(u32, 5000);

const a0: i32 = undefined;
var b0: u32 = undefined;

////////////////////////////////////////////////////////////////////////////////
// ARRAYS

const a = [5]u8{'h', 'e', 'l', 'l', 'o'};
const b = [5]u8{'h', 'e', 'l', 'l', 'o', '!', '!'};

fn two() void {
    std.debug.print("Hello, {d}!\n", 123);
}
