/**
 * Created by Thomas on 10/05/2017.
 */
function getURL(index, id) {
    var currentLocation = new String(window.location);
    var paths = currentLocation.split("/");
    document.getElementById(id).innerHTML = paths[index];
    return paths[index];
}