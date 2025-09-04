(function() {
    "use strict";

    let inputField;
    let searchResults;
    let resultsElement;

    shh.addOnload(function () {
        presentAll();
    });

    function presentAll() {
        hideSpinner();
        const element = document.getElementById("content");
        shh.removeChildren(element);
        element.appendChild(createElementWithText("h1", "Active Directory Group Search"));
        const inputDiv = shh.div();
        inputDiv.style.width = "100%";
        inputField = document.createElement("input");
        inputField.type = "text";
        inputField.onchange = function () { searchByInput(); }
        const inputButton = createElementWithText("button", "Search");
        inputButton.onclick = function () { searchByInput(); }
        inputDiv.appendChild(inputField);
        inputDiv.appendChild(inputButton);
        element.appendChild(inputDiv);
        resultsElement = shh.div();
        element.appendChild(resultsElement);
        presentResults();
        inputField.focus();
        const params = shh.getQueryParameters();
        if (params["dn"]) {
            getDn(params["dn"]);
        } else if (params["search"]) {
            search(params["search"]);
        }
    }

    function presentResults() {
        if (!resultsElement) {
            return;
        }
        shh.removeChildren(resultsElement);
        if (!searchResults) {
            return;
        }
        if (searchResults.error) {
            const errorDiv = shh.div("error");
            errorDiv.appendChild(createElementWithText("p", searchResults.error));
            resultsElement.appendChild(errorDiv);
            return;
        }
        const keys = Object.keys(searchResults.objects);
        keys.sort();
        if (keys.length === 0) {
            resultsElement.appendChild(createElementWithText("p", "No results found"));
            return;
        }
        keys.forEach(function (key) {
            const value = searchResults.objects[key];
            const h2 = document.createElement("h2");
            h2.appendChild(anchoredDn(key));
            resultsElement.appendChild(h2);
            if (value.members.length !== 0) {
                resultsElement.appendChild(createMemberDiv("Members", value.members));
            }
            if (value.memberOf.length !== 0) {
                resultsElement.appendChild(createMemberDiv("Groups", value.memberOf));
            }
        });
    }

    function createMemberDiv(heading, list) {
        const div = shh.div("indented");
        div.appendChild(createElementWithText("h3", heading));
        const ul = document.createElement("ul");
        list.sort().forEach(function (member) {
            const li = document.createElement("li");
            li.appendChild(anchoredDn(member));
            ul.appendChild(li);
        })
        div.appendChild(ul);
        return div;
    }

    function searchByInput() {
        search(inputField.value);
    }

    function search(query) {
        showSpinner();
        shh.httpGet("/api/v1/search/" + encodeURIComponent(query), function (xhr) {
            if (xhr.status === 200) {
                searchResults = JSON.parse(xhr.responseText);
                shh.updateLocation("search", query);
                if (inputField.value !== query) {
                    inputField.value = query;
                }
                presentResults();
            }
            hideSpinner();
        }, function (xhr) {
            shh.log("There was an error loading the results. code=" + xhr.status + ", text=" + xhr.statusText);
        });
    }

    function getDn(dn) {
        showSpinner();
        shh.httpGet("/api/v1/dn/" + encodeURIComponent(dn), function (xhr) {
            if (xhr.status === 200) {
                searchResults = JSON.parse(xhr.responseText);
                presentResults();
            }
            hideSpinner();
        }, function (xhr) {
            shh.log("There was an error loading the results. code=" + xhr.status + ", text=" + xhr.statusText);
        });
    }

    function anchoredDn(dn) {
        const a = createElementWithText("a", dn);
        a.href = "/?dn=" + encodeURIComponent(dn);
        return a;
    }

    function createElementWithText(tag, text) {
        const element = document.createElement(tag);
        element.appendChild(document.createTextNode(text));
        return element;
    }

    function showSpinner() {
        document.getElementById("spinner-overlay").style.display = "flex";
    }

    function hideSpinner() {
        document.getElementById("spinner-overlay").style.display = "none";
    }

})();
