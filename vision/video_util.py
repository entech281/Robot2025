import pyudev as pu

# Initialize the context for accessing udev
context = pu.Context()


class Fields:
    BUSNUM = 'BUSNUM'
    DEVICENAME = "DEVNAME"
    DEVICEPATH = "DEVPATH"

class HardwareValues:
    TOP_PORT = "fc88"
    BOTTOM_PORT = "fc80"
    SIDE_PORT = "xhci"

def add_properties(device, d ):
    n = {}
    n.update(d)
    if device is not None:
        for k in device.keys():
           n[k] = device.get(k)
    return n


def device_as_dict(device):
   d = {}
   d = add_properties(device.parent.parent,d)
   d = add_properties(device.parent,d)
   d = add_properties(device,d)
   return d


def load_video_capture_devices():
  capture_devices = []
  for d in context.list_devices(subsystem='video4linux'):
    q = device_as_dict(d)

    if 'ID_V4L_CAPABILITIES' in q.keys():
        if q['ID_V4L_CAPABILITIES'] == ':capture:':
           capture_devices.append(q)
  return capture_devices


ALL_CAPTURE_DEVICES = load_video_capture_devices()


def find_device_by_attribute(attr_name, attr_value ):
    for d in ALL_CAPTURE_DEVICES:
       if d[attr_name] == attr_value:
         return d
    return None

def device_by_bus(bus_num):
    d = find_device_by_attribute(Fields.BUSNUM,bus_num)
    return d

def get_devices_by_location():
    r = {}


    for d in ALL_CAPTURE_DEVICES:
        device_path = d[Fields.DEVICEPATH]
        if device_path.find(HardwareValues.TOP_PORT) >= 0:

            r['top'] = d

        elif device_path.find(HardwareValues.BOTTOM_PORT) >= 0:
            r['bottom'] = d

        elif device_path.find(HardwareValues.SIDE_PORT ) >= 0:
            r['side']= d


    return r

def make_simple_device_map():
    simple_device_map = {}
    for d in ALL_CAPTURE_DEVICES:
        bus_num = d[Fields.BUSNUM]
        device = d[Fields.DEVICENAME]
        device

        simple_device_map[bus_num] = device
    return simple_device_map

DEVICE_MAP = make_simple_device_map()

if __name__ ==  '__main__':
    d = get_devices_by_location()
    for k,v in d.items():
       print(k," ", v['DEVNAME'],"->",v['DEVPATH']," ", v['ID_USB_MODEL']," ", v['ID_PATH_TAG'])
