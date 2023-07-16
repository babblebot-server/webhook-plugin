# Webhook Plugin 

This is a Java plugin for a Discord bot that allows you to send webhook messages. 
The plugin is designed to be used with BabbleBot via Discord.

### Defining a hook
Currently, it is required to define all hooks using YAML format. These hooks can be either of the POST or GET variety, with the possibility of adding more methods in the future if necessary [TODO]. It is important to note that even for GET requests, the body must be defined.
```yaml
webhook:
  - name: "aaron"
    method: "GET"
    url: "https://example.com"
    body: ""
    headers:
      authorization: "Bearer 123456"
      content-type: "application/json"

  - name: "ben"
    method: "POST"
    url: "https://example.com"
    body: "{ \"key\": \"value\" }"
    headers:
      Content-Type: "application/json"
```

### Calling a hook

```properties
/<COMMAND_NAMESPACE>-send <HOOKNAME>
```

For example:
```properties
/webhook-send hookName
```

Feels free to create an [issue](https://github.com/babblebot-server/webhook-plugin/issues) if something doesn't work or you want additional functionality.


## Disclaimer
The software is provided "as is" without any warranty or guarantee of compatibility with other software or systems. By using the software, you acknowledge and accept any potential risks and waive any legal claims for faults. The developer/manufacturer does not offer technical support, but you may create an issue on GitHub for assistance. Please be aware that such communication should not be considered as formal technical support, and the developer/manufacturer cannot be held responsible for any damages or issues arising from the use of the software.