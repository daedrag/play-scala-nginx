(function() {
	
	var currentTable = undefined;
	var ws = undefined;
	
	var tableInput = document.getElementById('tableInput');
	var watchTableBtn = document.getElementById('watchTableButton');
	
	var itemInput = document.getElementById('itemInput');
	var addItemBtn = document.getElementById('addItemButton');
	var clearAllItemsBtn = document.getElementById('clearAllItemsButton');
	
	var statusElem = document.getElementById('status');
	var itemContainer = document.getElementById('items');
	
	function setStatus(status) {
		statusElem.innerHTML = status;
	}
	
	function addNewItem(item) {
		var elem = document.createElement('div');
		var t = document.createTextNode(item);
		elem.appendChild(t);
		itemContainer.appendChild(elem);
	}
	
	function clearAllItems(e) {
		itemContainer.innerHTML = '';
	}
	
	function connect(tableId) {
		if (!tableId) {
			return;
		}
		
		closingTable = currentTable;
		currentTable = tableId;
		if (ws) {
			ws.close();	
		}
		
		var wsUrl = 'wss://localhost:9005/ws/orders/table/' + tableId;
		ws = new WebSocket(wsUrl);
		ws.onmessage = (event) => {
			console.log('Data:', event);
			addNewItem(event.data);
		};
		
		ws.onerror = (event) => {
			console.error('Error:', event);
			setStatus('Error at table ' + tableId);
		};
		
		ws.onclose = (event) => {
			console.warn('Socket closed:', event);
			if (currentTable === tableId) {
				setStatus('Socket closed at table ' + tableId);	
			}
		};

		console.log('WS created', ws);
		setStatus('Connected to table ' + tableId);
	}
	
	function watchTable(e) {
		var tableId = tableInput.value;
		if (!tableId) return;

		connect(tableId);
	}
	
	function sendItem(e) {
		var item = itemInput.value;
		if (!item) return;
		
		var url = 'https://localhost:9005/orders/table/' + currentTable + '/item/' + item;
		var xmlHttp = new XMLHttpRequest();
	    xmlHttp.open("GET", url, false); // false for synchronous request
	    xmlHttp.send(null);
	}

	watchTableBtn.onclick = watchTable;
	addItemBtn.onclick = sendItem;
	clearAllItemsBtn.onclick = clearAllItems;
})();