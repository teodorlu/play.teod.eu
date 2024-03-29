const std = @import("std");

pub fn main() !void {
    // Prints to stderr (it's a shortcut based on `std.io.getStdErr()`)
    std.debug.print("All your {s} are belong to us.\n", .{"codebase"});

    // stdout is for the actual output of your application, for example if you
    // are implementing gzip, then only the compressed bytes should be sent to
    // stdout, not any debugging messages.
    const stdout_file = std.io.getStdOut().writer();
    var bw = std.io.bufferedWriter(stdout_file);
    const stdout = bw.writer();

    try stdout.print("Run `zig build test` to run the tests.\n", .{});

    try bw.flush(); // don't forget to flush!
}

test "simple test" {
    var list = std.ArrayList(i32).init(std.testing.allocator);
    defer list.deinit(); // try commenting this out and see if zig detects the memory leak!
    try list.append(42);
    try std.testing.expectEqual(@as(i32, 42), list.pop());
}

// Want to parse json into structs
// want to write tests

test "json test" {
    const x = 3;
    try std.testing.expectEqual(x, 3);
}

const pj =
    \\ {
    \\   "x": 1
    \\   "y": 2
    \\ }
;

// Note.
//
// // -- comment
// \\ -- multiline string!!!

const json = std.json;
const testing = std.testing;
const parseFromSlice = json.static.parseFromSlice;

const Point = struct {
    x: i32,
    y: i32,
};

test "json pj" {
    // try parseFromSlice
    // var gpa = std.heap.GeneralPurposeAllocator(.{}){};
    // const allocator = gpa.allocator();

    // const point = try std.json.parseFromSlice(Point, allocator, pj, .{});
    // try std.testing.expectEqual(point, point);

    // const stream = std.json.TokenStream.init(pj);
    // try std.testing.expectEqual(stream, stream);
}
