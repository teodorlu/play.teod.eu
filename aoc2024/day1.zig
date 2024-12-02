const std = @import("std");

const example3 =
    \\ 3   4
    \\ 4   3
    \\ 2   5
    \\ 1   3
    \\ 3   9
    \\ 3   3
;

fn distance(a: i32, b: i32) u32 {
    return @abs(a - b);
}

pub fn main() !void {
    var lines = std.mem.tokenizeScalar(u8, example3, '\n');
    while (lines.next()) |line| {
        std.debug.print("{s}\n", .{line});
        var cells = std.mem.tokenizeScalar(u8, line, ' ');
        const left = try std.fmt.parseInt(i32, cells.next().?, 10);
        const right = try std.fmt.parseInt(i32, cells.next().?, 10);

        std.debug.print("Sum      : {d}\n", .{left + right});
        std.debug.print("Distance : {d}\n", .{distance(left, right)});
    }
}

fn junk2() !void {
    var lineTokenizer = std.mem.tokenizeScalar(u8, example3, '\n');
    // const firstLine = lineTokenizer.next().?;
    std.debug.print("{s}\n", .{lineTokenizer.next().?});
    std.debug.print("{s}\n", .{lineTokenizer.next().?});
    std.debug.print("{s}\n", .{lineTokenizer.next().?});
    std.debug.print("{s}\n", .{lineTokenizer.next().?});
}

fn junk1() !void {
    //    const stdin = std.io.getStdIn().reader();
    //    const line = try stdin.readUntilDelimiterAlloc(std.heap.page_allocator, '\n', std.math.maxInt(usize));
    //    defer std.heap.page_allocator.free(line);
    //
    //    std.debug.print("You entered {s}\n", .{line});
    //
    //    const aStringWithANumber = "34";
    //    const firstNumber = try std.fmt.parseInt(i32, aStringWithANumber, 10);
    //    std.debug.print("Parsed ingteger: {d}\n", .{firstNumber});

    // const aLine = "3 4";
    // const delimiters = " ";
    // var tokenizer = std.mem.tokenize(aLine, delimiters);
    // while (tokenizer.next()) |token| {
    //     std.debug.print("Token: {s}\n", .{token});
    // }
}
