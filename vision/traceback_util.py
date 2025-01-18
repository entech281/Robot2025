import traceback


def get_stack_trace_lines(exception, wrap_length=80):
    """
    Accepts an exception object and returns a list of strings
    representing the stack trace for the exception, including
    tilde characters underlining the source of the error as
    seen with traceback.print_exc(). Lines longer than the specified
    wrap_length will be wrapped to the next line.

    :param exception: Exception object to extract the stack trace from.
    :param wrap_length: Maximum length of a line before wrapping.
    :return: List of strings corresponding to the stack trace lines.
    """
    raw_lines = traceback.format_exc().splitlines(keepends=False)
    wrapped_lines = []

    for line in raw_lines:
        while len(line) > wrap_length:
            wrapped_lines.append(line[:wrap_length])
            line = line[wrap_length:]
        wrapped_lines.append(line)

    return wrapped_lines
