Note that the OSGi service implementation declared in javalocator.xml is
referenced in the host bundle and not in this fragment. This is because
fragment bundles do not generate bundle started  events since they are
considered an intrinsic part of the host bundle. As there is no bundle started
event, DS doesn't know the bundle exists.

However, XML documents referenced by a bundle's Service-Component manifest
header may be contained in attached fragments.