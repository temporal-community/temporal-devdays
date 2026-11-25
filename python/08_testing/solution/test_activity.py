from temporalio.testing import ActivityEnvironment

from activities import send_bill
from models import Bill


async def test_send_bill():
    # TODO 1 start: run send_bill inside an ActivityEnvironment and check the result is TPD-c1-12
    env = ActivityEnvironment()
    result = await env.run(send_bill, Bill("c1", 12))
    assert result == "TPD-c1-12"
    # TODO 1 end
