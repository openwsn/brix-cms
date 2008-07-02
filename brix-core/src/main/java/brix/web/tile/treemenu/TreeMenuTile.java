package brix.web.tile.treemenu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;

import brix.jcr.api.JcrNodeIterator;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.page.tile.Tile;
import brix.plugin.site.page.tile.admin.TileEditorPanel;
import brix.web.reference.Reference;

public class TreeMenuTile implements Tile
{

    public TileEditorPanel newEditor(String id, IModel<BrixNode> containerNode)
    {
        return new TreeMenuTileEditorPanel(id, containerNode);
    }

    public String getDisplayName()
    {
        return "Tree Menu";
    }

    public Component newViewer(String id, IModel<BrixNode> tileNode)
    {
        return new TreeMenuRenderer(id, tileNode);
    }

    public static class Item implements Serializable, IDetachable
    {
        private String name;
        private Reference reference;
        private List<Item> children;
        private String itemCssId;
        private String containerCssId;

        Item()
        {
        }

        Item(String name)
        {
            super();
            this.name = name;
        }

        public String getItemCssId()
        {
            return itemCssId;
        }

        public void setItemCssId(String itemCssId)
        {
            this.itemCssId = itemCssId;
        }

        public String getContainerCssId()
        {
            return containerCssId;
        }

        public void setContainerCssId(String containerCssId)
        {
            this.containerCssId = containerCssId;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public Reference getReference()
        {
            if (reference == null)
            {
                reference = new Reference();
            }
            return reference;
        }

        public void setReference(Reference reference)
        {
            this.reference = reference;
        }

        public List<Item> getChildren()
        {
            if (children == null)
            {
                children = new ArrayList<Item>(1);
            }
            return children;
        }

        public void setChildren(List<Item> children)
        {
            this.children = children;
        }

        public void save(BrixNode node)
        {
            node.setProperty("name", getName());
            node.setProperty("itemCssId", getItemCssId());
            node.setProperty("containerCssId", getContainerCssId());
            getReference().save(node, "reference");
            for (Item item : children)
            {
                BrixNode child = (BrixNode) node.addNode("child");
                item.save(child);
            }
        }

        public void load(BrixNode node)
        {
            if (node.hasProperty("name"))
                setName(node.getProperty("name").getString());
            if (node.hasProperty("itemCssId"))
                setItemCssId(node.getProperty("itemCssId").getString());
            if (node.hasProperty("containerCssId"))
                setContainerCssId(node.getProperty("containerCssId").getString());
            setReference(Reference.load(node, "reference"));
            List<Item> children = new ArrayList<Item>();
            JcrNodeIterator childNodes = node.getNodes("child");
            while (childNodes.hasNext())
            {
                BrixNode child = (BrixNode) childNodes.nextNode();
                Item item = new Item();
                item.load(child);
                children.add(item);
            }
            setChildren(children);
        }

        public void detach()
        {
            if (reference != null)
            {
                reference.detach();
            }
            if (children != null)
            {
                for (Item child : children)
                {
                    child.detach();
                }
            }
        }

    }

    public static class RootItem extends Item
    {
        RootItem(String name, String url)
        {
            super(name);
        }

        RootItem()
        {

        }

        private String selectedCssClass;
        private String version;

        public String getSelectedCssClass()
        {
            return selectedCssClass;
        }

        public void setSelectedCssClass(String selectedCssClass)
        {
            this.selectedCssClass = selectedCssClass;
        }

        public String getVersion()
        {
            return version;
        }

        public void setVersion(String version)
        {
            this.version = version;
        }

        @Override
        public void save(BrixNode node)
        {
            node.setProperty("selectedCssClass", getSelectedCssClass());
            node.setProperty("version", getVersion());
            super.save(node);
        }

        @Override
        public void load(BrixNode node)
        {
            super.load(node);
            if (node.hasProperty("selectedCssClass"))
                setSelectedCssClass(node.getProperty("selectedCssClass").getString());
            if (node.hasProperty("version"))
                setVersion(node.getProperty("version").getString());
        }

    }

    public static void save(RootItem item, BrixNode tileNode)
    {
        if (tileNode.hasNode("data"))
        {
            BrixNode data = (BrixNode) tileNode.getNode("data");
            data.remove();
        }
        BrixNode data = (BrixNode) tileNode.addNode("data");
        item.save(data);
    }

    public static void load(RootItem item, BrixNode tileNode)
    {
        if (tileNode.hasNode("data"))
        {
            BrixNode data = (BrixNode) tileNode.getNode("data");
            item.load(data);
        }
    }

    public String getTypeName()
    {
        return "brix.web.tile.treemenu.TreeMenuTile";
    }

    public boolean requiresSSL(IModel<BrixNode> data)
    {
        return false;
    }
}
