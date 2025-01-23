import pytest
import traceback_util
import traceback
def testSimpleException():
    try:
        d = 1/0
    except Exception as e:
        lines = traceback_util.get_stack_trace_lines(e,40)
        assert "Traceback" == lines[0][0:9]
        assert 'ZeroDivisionError: division by zero' == lines[6]


